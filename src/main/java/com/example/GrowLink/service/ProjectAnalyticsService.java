package com.example.GrowLink.service;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.GrowLink.dto.ProjectAnalyticsDto;
import com.example.GrowLink.entity.Project;
import com.example.GrowLink.entity.User;
import com.example.GrowLink.enums.TaskPriority;
import com.example.GrowLink.enums.TaskStatus;
import com.example.GrowLink.repository.ProjectMemberRepository;
import com.example.GrowLink.repository.ProjectRepository;
import com.example.GrowLink.repository.ProjectTaskRepository;
import com.example.GrowLink.repository.UserRepository;

import java.time.LocalDate;

@Service
@Transactional(readOnly = true)
public class ProjectAnalyticsService {

    private final ProjectRepository projectRepository;
    private final ProjectTaskRepository projectTaskRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final UserRepository userRepository;

    public ProjectAnalyticsService(ProjectRepository projectRepository,
                                   ProjectTaskRepository projectTaskRepository,
                                   ProjectMemberRepository projectMemberRepository,
                                   UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.projectTaskRepository = projectTaskRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.userRepository = userRepository;
    }

    public Project getProjectById(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found."));
    }

    public boolean isOwner(String email, Long projectId) {
        Project project = getProjectById(projectId);
        return project.getOwner() != null
                && project.getOwner().getEmail() != null
                && project.getOwner().getEmail().equalsIgnoreCase(email);
    }

    public boolean isMember(String email, Long projectId) {
        Project project = getProjectById(projectId);
        User user = getUserByEmail(email);
        return projectMemberRepository.existsByProjectAndUser(project, user);
    }

    public ProjectAnalyticsDto getAnalytics(Long projectId, String userEmail) {
        Project project = getProjectById(projectId);

        boolean owner = isOwner(userEmail, projectId);
        boolean member = isMember(userEmail, projectId);

        if (!owner && !member) {
            throw new AccessDeniedException("You do not have access to project analytics.");
        }

        long totalTasks = projectTaskRepository.countByProject(project);
        long todoTasks = projectTaskRepository.countByProjectAndStatus(project, TaskStatus.TODO);
        long inProgressTasks = projectTaskRepository.countByProjectAndStatus(project, TaskStatus.IN_PROGRESS);
        long doneTasks = projectTaskRepository.countByProjectAndStatus(project, TaskStatus.DONE);
        long totalMembers = projectMemberRepository.findByProjectOrderByIdAsc(project).size();
        long overdueTasks = projectTaskRepository.countByProjectAndDueDateBeforeAndStatusNot(
                project, LocalDate.now(), TaskStatus.DONE
        );
        long unassignedTasks = projectTaskRepository.countByProjectAndAssignedUserIsNull(project);
        long highPriorityTasks = projectTaskRepository.countByProjectAndPriority(project, TaskPriority.HIGH);

        int completionPercentage = 0;
        if (totalTasks > 0) {
            completionPercentage = (int) Math.round((doneTasks * 100.0) / totalTasks);
        }

        ProjectAnalyticsDto dto = new ProjectAnalyticsDto();
        dto.setTotalTasks(totalTasks);
        dto.setTodoTasks(todoTasks);
        dto.setInProgressTasks(inProgressTasks);
        dto.setDoneTasks(doneTasks);
        dto.setTotalMembers(totalMembers);
        dto.setOverdueTasks(overdueTasks);
        dto.setUnassignedTasks(unassignedTasks);
        dto.setHighPriorityTasks(highPriorityTasks);
        dto.setCompletionPercentage(completionPercentage);

        return dto;
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
    }
}