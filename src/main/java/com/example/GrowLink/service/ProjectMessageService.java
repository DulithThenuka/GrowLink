package com.example.GrowLink.service;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.GrowLink.dto.ProjectMessageDto;
import com.example.GrowLink.entity.Project;
import com.example.GrowLink.entity.ProjectMessage;
import com.example.GrowLink.entity.User;
import com.example.GrowLink.repository.ProjectMemberRepository;
import com.example.GrowLink.repository.ProjectMessageRepository;
import com.example.GrowLink.repository.ProjectRepository;
import com.example.GrowLink.repository.UserRepository;

@Service
@Transactional
public class ProjectMessageService {

    private final ProjectMessageRepository projectMessageRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final UserRepository userRepository;

    public ProjectMessageService(ProjectMessageRepository projectMessageRepository,
                                 ProjectRepository projectRepository,
                                 ProjectMemberRepository projectMemberRepository,
                                 UserRepository userRepository) {
        this.projectMessageRepository = projectMessageRepository;
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
    public List<ProjectMessage> getMessagesByProjectId(Long projectId) {
        Project project = getProjectById(projectId);
        return projectMessageRepository.findByProjectOrderByIdDesc(project);
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

    @Transactional(readOnly = true)
    public long getMessageCount(Long projectId) {
        Project project = getProjectById(projectId);
        return projectMessageRepository.countByProject(project);
    }

    public String sendMessage(Long projectId, String userEmail, ProjectMessageDto dto) {
        Project project = getProjectById(projectId);

        boolean owner = isOwner(userEmail, projectId);
        boolean member = isMember(userEmail, projectId);

        if (!owner && !member) {
            throw new AccessDeniedException("Only project members can send messages.");
        }

        User sender = getUserByEmail(userEmail);

        ProjectMessage message = new ProjectMessage();
        message.setProject(project);
        message.setSender(sender);
        message.setContent(dto.getContent().trim());

        projectMessageRepository.save(message);

        return "Message sent successfully.";
    }

    public String deleteMessage(Long projectId, Long messageId, String userEmail) {
        Project project = getProjectById(projectId);

        ProjectMessage message = projectMessageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Message not found."));

        if (message.getProject() == null || !message.getProject().getId().equals(project.getId())) {
            throw new IllegalArgumentException("Message does not belong to this project.");
        }

        User currentUser = getUserByEmail(userEmail);

        boolean owner = isOwner(userEmail, projectId);
        boolean sender = message.getSender() != null
                && message.getSender().getId() != null
                && message.getSender().getId().equals(currentUser.getId());

        if (!owner && !sender) {
            throw new AccessDeniedException("You do not have permission to delete this message.");
        }

        projectMessageRepository.delete(message);
        return "Message deleted successfully.";
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
    }
}