package com.example.GrowLink.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.GrowLink.dto.ProjectDto;
import com.example.GrowLink.entity.Project;
import com.example.GrowLink.entity.ProjectJoinRequest;
import com.example.GrowLink.entity.ProjectMember;
import com.example.GrowLink.entity.User;
import com.example.GrowLink.enums.ProjectRole;
import com.example.GrowLink.enums.ProjectStatus;
import com.example.GrowLink.enums.RequestStatus;
import com.example.GrowLink.repository.ProjectJoinRequestRepository;
import com.example.GrowLink.repository.ProjectMemberRepository;
import com.example.GrowLink.repository.ProjectRepository;
import com.example.GrowLink.repository.UserRepository;

@Service
@Transactional
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectJoinRequestRepository projectJoinRequestRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final UserRepository userRepository;

    public ProjectService(ProjectRepository projectRepository,
                          ProjectJoinRequestRepository projectJoinRequestRepository,
                          ProjectMemberRepository projectMemberRepository,
                          UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.projectJoinRequestRepository = projectJoinRequestRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.userRepository = userRepository;
    }

    public void createProject(String email, ProjectDto projectDto) {
        User owner = getUserByEmail(email);

        Project project = new Project();

        project.setTitle(projectDto.getTitle());
        project.setDescription(projectDto.getDescription());
        project.setCategory(projectDto.getCategory());
        project.setStatus(projectDto.getStatus() != null ? projectDto.getStatus() : ProjectStatus.OPEN);
        project.setOwner(owner);

        Project savedProject = projectRepository.save(project);

        ProjectMember ownerMember = new ProjectMember();
        ownerMember.setProject(savedProject);
        ownerMember.setUser(owner);
        ownerMember.setRole(ProjectRole.OWNER);
        projectMemberRepository.save(ownerMember);
    }

    @Transactional(readOnly = true)
    public List<Project> searchProjects(String keyword, String category) {
        boolean hasKeyword = keyword != null && !keyword.isBlank();
        boolean hasCategory = category != null && !category.isBlank();

        if (hasKeyword && hasCategory) {
            return projectRepository
                    .findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndCategoryIgnoreCaseOrderByIdDesc(
                            keyword, keyword, category
                    );
        }

        if (hasKeyword) {
            return projectRepository
                    .findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrderByIdDesc(keyword, keyword);
        }

        if (hasCategory) {
            return projectRepository.findByCategoryIgnoreCaseOrderByIdDesc(category);
        }

        return projectRepository.findAll()
                .stream()
                .sorted((a, b) -> Long.compare(b.getId(), a.getId()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Project> getProjectsByOwnerEmail(String email) {
        User user = getUserByEmail(email);
        return projectRepository.findByOwnerOrderByIdDesc(user);
    }

    @Transactional(readOnly = true)
    public List<Project> getProjectsJoinedByUserEmail(String email) {
        User user = getUserByEmail(email);
        List<ProjectMember> memberships = projectMemberRepository.findByUserOrderByIdDesc(user);

        List<Project> projects = new ArrayList<>();
        for (ProjectMember membership : memberships) {
            if (membership.getProject() != null) {
                projects.add(membership.getProject());
            }
        }

        return projects.stream().distinct().toList();
    }

    @Transactional(readOnly = true)
    public Project getProjectById(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found."));
    }

    @Transactional(readOnly = true)
    public List<ProjectMember> getMembersByProjectId(Long projectId) {
        Project project = getProjectById(projectId);
        return projectMemberRepository.findByProjectOrderByIdAsc(project);
    }

    @Transactional(readOnly = true)
    public List<ProjectJoinRequest> getJoinRequestsByProjectId(Long projectId) {
        Project project = getProjectById(projectId);
        return projectJoinRequestRepository.findByProjectAndStatusOrderByIdDesc(project, RequestStatus.PENDING);
    }

    @Transactional(readOnly = true)
    public List<String> getRequiredSkillsByProjectId(Long projectId) {
        return new ArrayList<>();
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
    public boolean hasPendingJoinRequest(String email, Long projectId) {
        Project project = getProjectById(projectId);
        User user = getUserByEmail(email);
        return projectJoinRequestRepository.existsByProjectAndUserAndStatus(project, user, RequestStatus.PENDING);
    }

    public String sendJoinRequest(String email, Long projectId) {
        Project project = getProjectById(projectId);
        User user = getUserByEmail(email);

        if (project.getOwner() != null && project.getOwner().getId().equals(user.getId())) {
            return "You already own this project.";
        }

        if (projectMemberRepository.existsByProjectAndUser(project, user)) {
            return "You are already a member of this project.";
        }

        if (projectJoinRequestRepository.existsByProjectAndUserAndStatus(project, user, RequestStatus.PENDING)) {
            return "You already have a pending join request.";
        }

        ProjectJoinRequest request = projectJoinRequestRepository.findByProjectAndUser(project, user)
                .orElse(new ProjectJoinRequest());

        request.setProject(project);
        request.setUser(user);
        request.setStatus(RequestStatus.PENDING);
        request.setMessage("I would like to join this project.");
        projectJoinRequestRepository.save(request);

        return "Join request sent successfully.";
    }

    public String cancelJoinRequest(String email, Long projectId) {
        Project project = getProjectById(projectId);
        User user = getUserByEmail(email);

        ProjectJoinRequest request = projectJoinRequestRepository.findByProjectAndUser(project, user)
                .orElseThrow(() -> new IllegalArgumentException("Join request not found."));

        if (request.getStatus() != RequestStatus.PENDING) {
            return "Only pending requests can be cancelled.";
        }

        projectJoinRequestRepository.delete(request);
        return "Join request cancelled.";
    }

    public String updateProjectStatus(String email, Long projectId, ProjectStatus status) {
        Project project = getProjectById(projectId);

        if (!isOwner(email, projectId)) {
            throw new AccessDeniedException("Only the project owner can update status.");
        }

        project.setStatus(status);
        projectRepository.save(project);

        return "Project status updated successfully.";
    }

    public String acceptJoinRequest(String ownerEmail, Long requestId) {
        ProjectJoinRequest request = projectJoinRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Join request not found."));

        Project project = request.getProject();

        if (project == null) {
            throw new IllegalArgumentException("Project not found.");
        }

        if (project.getOwner() == null || !project.getOwner().getEmail().equalsIgnoreCase(ownerEmail)) {
            throw new AccessDeniedException("Only the project owner can accept requests.");
        }

        if (request.getStatus() != RequestStatus.PENDING) {
            return "This request is already processed.";
        }

        request.setStatus(RequestStatus.ACCEPTED);
        projectJoinRequestRepository.save(request);

        if (!projectMemberRepository.existsByProjectAndUser(project, request.getUser())) {
            ProjectMember member = new ProjectMember();
            member.setProject(project);
            member.setUser(request.getUser());
            member.setRole(ProjectRole.MEMBER);
            projectMemberRepository.save(member);
        }

        return "Join request accepted successfully.";
    }

    public String rejectJoinRequest(String ownerEmail, Long requestId) {
        ProjectJoinRequest request = projectJoinRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Join request not found."));

        Project project = request.getProject();

        if (project == null) {
            throw new IllegalArgumentException("Project not found.");
        }

        if (project.getOwner() == null || !project.getOwner().getEmail().equalsIgnoreCase(ownerEmail)) {
            throw new AccessDeniedException("Only the project owner can reject requests.");
        }

        if (request.getStatus() != RequestStatus.PENDING) {
            return "This request is already processed.";
        }

        request.setStatus(RequestStatus.REJECTED);
        projectJoinRequestRepository.save(request);

        return "Join request rejected successfully.";
    }

    public String removeMember(String ownerEmail, Long projectId, Long memberUserId) {
        Project project = getProjectById(projectId);

        if (!isOwner(ownerEmail, projectId)) {
            throw new AccessDeniedException("Only the project owner can remove members.");
        }

        User memberUser = userRepository.findById(memberUserId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        if (project.getOwner() != null && project.getOwner().getId().equals(memberUserId)) {
            return "Owner cannot be removed from the project.";
        }

        projectMemberRepository.deleteByProjectAndUser(project, memberUser);
        return "Member removed successfully.";
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
    }
}