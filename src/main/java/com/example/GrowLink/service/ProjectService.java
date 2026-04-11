package com.example.GrowLink.service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.GrowLink.dto.ProjectDto;
import com.example.GrowLink.entity.Project;
import com.example.GrowLink.entity.ProjectJoinRequest;
import com.example.GrowLink.entity.ProjectMember;
import com.example.GrowLink.entity.ProjectRequiredSkill;
import com.example.GrowLink.entity.User;
import com.example.GrowLink.enums.NotificationType;
import com.example.GrowLink.enums.ProjectRole;
import com.example.GrowLink.enums.ProjectStatus;
import com.example.GrowLink.enums.RequestStatus;
import com.example.GrowLink.repository.ProjectJoinRequestRepository;
import com.example.GrowLink.repository.ProjectMemberRepository;
import com.example.GrowLink.repository.ProjectRepository;
import com.example.GrowLink.repository.ProjectRequiredSkillRepository;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectJoinRequestRepository projectJoinRequestRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectRequiredSkillRepository projectRequiredSkillRepository;
    private final UserService userService;
    private final NotificationService notificationService;

    public ProjectService(ProjectRepository projectRepository,
                          ProjectJoinRequestRepository projectJoinRequestRepository,
                          ProjectMemberRepository projectMemberRepository,
                          ProjectRequiredSkillRepository projectRequiredSkillRepository,
                          UserService userService,
                          NotificationService notificationService) {
        this.projectRepository = projectRepository;
        this.projectJoinRequestRepository = projectJoinRequestRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.projectRequiredSkillRepository = projectRequiredSkillRepository;
        this.userService = userService;
        this.notificationService = notificationService;
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public List<Project> getProjectsByOwnerEmail(String email) {
        User owner = userService.getUserByEmail(email);
        return projectRepository.findByOwner(owner);
    }

    public List<ProjectJoinRequest> getJoinRequestsByProjectId(Long projectId) {
        Project project = getProjectById(projectId);
        return projectJoinRequestRepository.findByProject(project);
    }

    public List<ProjectMember> getMembersByProjectId(Long projectId) {
        Project project = getProjectById(projectId);
        return projectMemberRepository.findByProject(project);
    }

    public List<ProjectMember> getProjectsJoinedByUserEmail(String email) {
        User user = userService.getUserByEmail(email);
        return projectMemberRepository.findByUser(user);
    }

    public List<ProjectRequiredSkill> getRequiredSkillsByProjectId(Long projectId) {
        Project project = getProjectById(projectId);
        return projectRequiredSkillRepository.findByProject(project);
    }

    public Project getProjectById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project not found."));
    }

    public boolean isOwner(String email, Long projectId) {
        Project project = getProjectById(projectId);
        return project.getOwner().getEmail().equals(email);
    }

    public boolean isMember(String email, Long projectId) {
        User user = userService.getUserByEmail(email);
        Project project = getProjectById(projectId);
        return projectMemberRepository.findByProjectAndUser(project, user).isPresent();
    }

    public List<Project> searchProjects(String keyword, String category, ProjectStatus status) {
        boolean hasKeyword = keyword != null && !keyword.isBlank();
        boolean hasCategory = category != null && !category.isBlank();
        boolean hasStatus = status != null;

        String cleanedKeyword = hasKeyword ? keyword.trim() : null;
        String cleanedCategory = hasCategory ? category.trim() : null;

        if (hasKeyword && hasCategory && hasStatus) {
            return projectRepository.findByTitleContainingIgnoreCaseAndCategoryContainingIgnoreCaseAndStatus(
                    cleanedKeyword,
                    cleanedCategory,
                    status
            );
        }

        if (hasKeyword && hasCategory) {
            return projectRepository.findByTitleContainingIgnoreCaseAndCategoryContainingIgnoreCase(
                    cleanedKeyword,
                    cleanedCategory
            );
        }

        if (hasKeyword && hasStatus) {
            return projectRepository.findByTitleContainingIgnoreCaseAndStatus(
                    cleanedKeyword,
                    status
            );
        }

        if (hasCategory && hasStatus) {
            return projectRepository.findByCategoryContainingIgnoreCaseAndStatus(
                    cleanedCategory,
                    status
            );
        }

        if (hasKeyword) {
            return projectRepository.findByTitleContainingIgnoreCase(cleanedKeyword);
        }

        if (hasCategory) {
            return projectRepository.findByCategoryContainingIgnoreCase(cleanedCategory);
        }

        if (hasStatus) {
            return projectRepository.findByStatus(status);
        }

        return projectRepository.findAll();
    }

    @Transactional
    public void createProject(String email, ProjectDto dto) {
        User owner = userService.getUserByEmail(email);

        Project project = new Project();
        project.setTitle(dto.getTitle().trim());
        project.setDescription(dto.getDescription().trim());
        project.setCategory(dto.getCategory() != null ? dto.getCategory().trim() : null);
        project.setStatus(ProjectStatus.OPEN);
        project.setOwner(owner);

        projectRepository.save(project);

        ProjectMember ownerMember = new ProjectMember();
        ownerMember.setProject(project);
        ownerMember.setUser(owner);
        ownerMember.setRole(ProjectRole.OWNER);

        projectMemberRepository.save(ownerMember);

        saveRequiredSkills(project, dto.getRequiredSkillsText());
    }

    @Transactional
    public String updateProjectStatus(String ownerEmail, Long projectId, ProjectStatus status) {
        Project project = getProjectById(projectId);

        if (!project.getOwner().getEmail().equals(ownerEmail)) {
            throw new AccessDeniedException("You are not allowed to update this project.");
        }

        project.setStatus(status);
        projectRepository.save(project);

        return "Project status updated successfully.";
    }

    @Transactional
    public String sendJoinRequest(String email, Long projectId) {
        User user = userService.getUserByEmail(email);
        Project project = getProjectById(projectId);

        if (project.getOwner().getId().equals(user.getId())) {
            return "You are already the owner of this project.";
        }

        if (project.getStatus() != ProjectStatus.OPEN) {
            return "This project is not open for joining.";
        }

        if (projectMemberRepository.findByProjectAndUser(project, user).isPresent()) {
            return "You are already a member of this project.";
        }

        if (projectJoinRequestRepository.findByProjectAndUser(project, user).isPresent()) {
            return "You already sent a join request for this project.";
        }

        ProjectJoinRequest joinRequest = new ProjectJoinRequest();
        joinRequest.setProject(project);
        joinRequest.setUser(user);
        joinRequest.setStatus(RequestStatus.PENDING);

        projectJoinRequestRepository.save(joinRequest);

        notificationService.createNotification(
                project.getOwner(),
                "New Project Join Request",
                user.getFullName() + " requested to join your project: " + project.getTitle(),
                NotificationType.SYSTEM
        );

        return "Join request sent successfully.";
    }

    @Transactional
    public String acceptJoinRequest(String ownerEmail, Long requestId) {
        ProjectJoinRequest request = projectJoinRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Join request not found."));

        Project project = request.getProject();

        if (!project.getOwner().getEmail().equals(ownerEmail)) {
            throw new AccessDeniedException("You are not allowed to manage this request.");
        }

        if (request.getStatus() != RequestStatus.PENDING) {
            return "This join request has already been processed.";
        }

        request.setStatus(RequestStatus.ACCEPTED);
        projectJoinRequestRepository.save(request);

        ProjectMember member = new ProjectMember();
        member.setProject(project);
        member.setUser(request.getUser());
        member.setRole(ProjectRole.MEMBER);

        projectMemberRepository.save(member);

        notificationService.createNotification(
                request.getUser(),
                "Project Request Accepted",
                "You were accepted into the project: " + project.getTitle(),
                NotificationType.SYSTEM
        );

        return "Join request accepted.";
    }

    @Transactional
    public String rejectJoinRequest(String ownerEmail, Long requestId) {
        ProjectJoinRequest request = projectJoinRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Join request not found."));

        Project project = request.getProject();

        if (!project.getOwner().getEmail().equals(ownerEmail)) {
            throw new AccessDeniedException("You are not allowed to manage this request.");
        }

        if (request.getStatus() != RequestStatus.PENDING) {
            return "This join request has already been processed.";
        }

        request.setStatus(RequestStatus.REJECTED);
        projectJoinRequestRepository.save(request);

        notificationService.createNotification(
                request.getUser(),
                "Project Request Rejected",
                "Your join request for the project \"" + project.getTitle() + "\" was rejected.",
                NotificationType.SYSTEM
        );

        return "Join request rejected.";
    }

    private void saveRequiredSkills(Project project, String requiredSkillsText) {
        if (requiredSkillsText == null || requiredSkillsText.isBlank()) {
            return;
        }

        Set<String> uniqueSkills = new LinkedHashSet<>();
        String[] parts = requiredSkillsText.split(",");

        for (String part : parts) {
            String skill = part.trim();
            if (!skill.isEmpty()) {
                uniqueSkills.add(skill);
            }
        }

        for (String skillName : uniqueSkills) {
            ProjectRequiredSkill requiredSkill = new ProjectRequiredSkill();
            requiredSkill.setProject(project);
            requiredSkill.setSkillName(skillName);
            projectRequiredSkillRepository.save(requiredSkill);
        }
    }
}