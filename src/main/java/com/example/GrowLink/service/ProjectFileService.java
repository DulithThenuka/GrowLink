package com.example.GrowLink.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.example.GrowLink.entity.Project;
import com.example.GrowLink.entity.ProjectFile;
import com.example.GrowLink.entity.User;
import com.example.GrowLink.repository.ProjectFileRepository;
import com.example.GrowLink.repository.ProjectMemberRepository;
import com.example.GrowLink.repository.ProjectRepository;
import com.example.GrowLink.repository.UserRepository;

@Service
@Transactional
public class ProjectFileService {

    private final ProjectFileRepository projectFileRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final UserRepository userRepository;

    private final Path uploadRoot = Paths.get("uploads", "project-files");

    public ProjectFileService(ProjectFileRepository projectFileRepository,
                              ProjectRepository projectRepository,
                              ProjectMemberRepository projectMemberRepository,
                              UserRepository userRepository) {
        this.projectFileRepository = projectFileRepository;
        this.projectRepository = projectRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public Project getProjectById(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found."));
    }

    @Transactional(readOnly = true)
    public List<ProjectFile> getFilesByProjectId(Long projectId) {
        Project project = getProjectById(projectId);
        return projectFileRepository.findByProjectOrderByIdDesc(project);
    }

    @Transactional(readOnly = true)
    public long getFileCount(Long projectId) {
        Project project = getProjectById(projectId);
        return projectFileRepository.countByProject(project);
    }

    @Transactional(readOnly = true)
    public boolean isOwner(String email, Long projectId) {
        Project project = getProjectById(projectId);
        return project.getOwner() != null
                && project.getOwner().getEmail() != null
                && project.getOwner().getEmail().equalsIgnoreCase(email);
    }

    @Transactional(readOnly = true)
    public boolean isMember(String email, Long projectId) {
        Project project = getProjectById(projectId);
        User user = getUserByEmail(email);
        return projectMemberRepository.existsByProjectAndUser(project, user);
    }

    public String uploadFile(Long projectId, String userEmail, MultipartFile multipartFile) {
        Project project = getProjectById(projectId);

        boolean owner = isOwner(userEmail, projectId);
        boolean member = isMember(userEmail, projectId);

        if (!owner && !member) {
            throw new AccessDeniedException("Only project members can upload files.");
        }

        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new IllegalArgumentException("Please select a file to upload.");
        }

        try {
            Files.createDirectories(uploadRoot);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create upload directory.");
        }

        String originalFileName = StringUtils.cleanPath(multipartFile.getOriginalFilename() != null
                ? multipartFile.getOriginalFilename()
                : "file");

        if (originalFileName.contains("..")) {
            throw new IllegalArgumentException("Invalid file name.");
        }

        String extension = "";
        int dotIndex = originalFileName.lastIndexOf('.');
        if (dotIndex >= 0) {
            extension = originalFileName.substring(dotIndex);
        }

        String storedFileName = UUID.randomUUID() + extension;
        Path destination = uploadRoot.resolve(storedFileName).normalize().toAbsolutePath();

        try (InputStream inputStream = multipartFile.getInputStream()) {
            Files.copy(inputStream, destination, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save uploaded file.");
        }

        User uploader = getUserByEmail(userEmail);

        ProjectFile projectFile = new ProjectFile();
        projectFile.setProject(project);
        projectFile.setUploadedBy(uploader);
        projectFile.setOriginalFileName(originalFileName);
        projectFile.setStoredFileName(storedFileName);
        projectFile.setFileType(multipartFile.getContentType());
        projectFile.setFileSize(multipartFile.getSize());

        projectFileRepository.save(projectFile);

        return "File uploaded successfully.";
    }

    @Transactional(readOnly = true)
    public ProjectFile getProjectFileById(Long fileId) {
        return projectFileRepository.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("File not found."));
    }

    @Transactional(readOnly = true)
    public Resource downloadFile(Long projectId, Long fileId, String userEmail) {
        Project project = getProjectById(projectId);

        boolean owner = isOwner(userEmail, projectId);
        boolean member = isMember(userEmail, projectId);

        if (!owner && !member) {
            throw new AccessDeniedException("Only project members can download files.");
        }

        ProjectFile projectFile = getProjectFileById(fileId);

        if (projectFile.getProject() == null || !projectFile.getProject().getId().equals(project.getId())) {
            throw new IllegalArgumentException("File does not belong to this project.");
        }

        try {
            Path filePath = uploadRoot.resolve(projectFile.getStoredFileName()).normalize().toAbsolutePath();
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                throw new IllegalArgumentException("Stored file not found.");
            }

            return resource;
        } catch (Exception e) {
            throw new RuntimeException("Failed to read file.");
        }
    }

    public String deleteFile(Long projectId, Long fileId, String userEmail) {
        Project project = getProjectById(projectId);
        ProjectFile projectFile = getProjectFileById(fileId);
        User currentUser = getUserByEmail(userEmail);

        if (projectFile.getProject() == null || !projectFile.getProject().getId().equals(project.getId())) {
            throw new IllegalArgumentException("File does not belong to this project.");
        }

        boolean owner = isOwner(userEmail, projectId);
        boolean uploader = projectFile.getUploadedBy() != null
                && projectFile.getUploadedBy().getId() != null
                && projectFile.getUploadedBy().getId().equals(currentUser.getId());

        if (!owner && !uploader) {
            throw new AccessDeniedException("You do not have permission to delete this file.");
        }

        try {
            Path filePath = uploadRoot.resolve(projectFile.getStoredFileName()).normalize().toAbsolutePath();
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete stored file.");
        }

        projectFileRepository.delete(projectFile);
        return "File deleted successfully.";
    }

    public String formatFileSize(Long bytes) {
        if (bytes == null) {
            return "0 B";
        }

        double size = bytes.doubleValue();
        String[] units = {"B", "KB", "MB", "GB"};
        int unitIndex = 0;

        while (size >= 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }

        return String.format("%.1f %s", size, units[unitIndex]);
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
    }
}