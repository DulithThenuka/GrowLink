package com.example.GrowLink.service;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.GrowLink.dto.ProjectTaskDto;
import com.example.GrowLink.entity.Project;
import com.example.GrowLink.entity.ProjectMember;
import com.example.GrowLink.entity.ProjectTask;
import com.example.GrowLink.entity.User;
import com.example.GrowLink.enums.NotificationType;
import com.example.GrowLink.enums.TaskPriority;
import com.example.GrowLink.enums.TaskStatus;
import com.example.GrowLink.repository.ProjectMemberRepository;
import com.example.GrowLink.repository.ProjectRepository;
import com.example.GrowLink.repository.ProjectTaskRepository;
import com.example.GrowLink.repository.UserRepository;

@Service
@Transactional
public class ProjectTaskService {

    private final ProjectTaskRepository projectTaskRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public ProjectTaskService(ProjectTaskRepository projectTaskRepository,
                              ProjectRepository projectRepository,
                              ProjectMemberRepository projectMemberRepository,
                              UserRepository userRepository,
                              NotificationService notificationService) {
        this.projectTaskRepository = projectTaskRepository;
        this.projectRepository = projectRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    @Transactional(readOnly = true)
    public Project getProjectById(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found."));
    }

    @Transactional(readOnly = true)
    public ProjectTask getTaskById(Long taskId) {
        return projectTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found."));
    }

    @Transactional(readOnly = true)
    public List<ProjectTask> getTasksByProjectId(Long projectId) {
        Project project = getProjectById(projectId);
        return projectTaskRepository.findByProjectOrderByIdDesc(project);
    }

    @Transactional(readOnly = true)
    public List<ProjectMember> getProjectMembers(Long projectId) {
        Project project = getProjectById(projectId);
        return projectMemberRepository.findByProjectOrderByIdAsc(project);
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
    public long getTodoCount(Long projectId) {
        Project project = getProjectById(projectId);
        return projectTaskRepository.countByProjectAndStatus(project, TaskStatus.TODO);
    }

    @Transactional(readOnly = true)
    public long getInProgressCount(Long projectId) {
        Project project = getProjectById(projectId);
        return projectTaskRepository.countByProjectAndStatus(project, TaskStatus.IN_PROGRESS);
    }

    @Transactional(readOnly = true)
    public long getDoneCount(Long projectId) {
        Project project = getProjectById(projectId);
        return projectTaskRepository.countByProjectAndStatus(project, TaskStatus.DONE);
    }

    public String createTask(Long projectId, String ownerEmail, ProjectTaskDto dto) {
        Project project = getProjectById(projectId);

        if (!isOwner(ownerEmail, projectId)) {
            throw new AccessDeniedException("Only the project owner can create tasks.");
        }

        User owner = getUserByEmail(ownerEmail);

        ProjectTask task = new ProjectTask();
        task.setProject(project);
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setPriority(dto.getPriority() != null ? dto.getPriority() : TaskPriority.MEDIUM);
        task.setStatus(TaskStatus.TODO);
        task.setDueDate(dto.getDueDate());

        if (dto.getAssignedUserId() != null) {
            User assignedUser = userRepository.findById(dto.getAssignedUserId())
                    .orElseThrow(() -> new IllegalArgumentException("Assigned user not found."));

            if (!projectMemberRepository.existsByProjectAndUser(project, assignedUser)) {
                throw new IllegalArgumentException("Assigned user is not a member of this project.");
            }

            task.setAssignedUser(assignedUser);
        }

        projectTaskRepository.save(task);

        if (task.getAssignedUser() != null && !task.getAssignedUser().getId().equals(owner.getId())) {
            notificationService.createNotification(
                    task.getAssignedUser(),
                    "New Task Assigned",
                    "You were assigned a task: " + task.getTitle() + " in project " + project.getTitle() + ".",
                    NotificationType.TASK
            );
        }

        return "Task created successfully.";
    }

    public String updateTaskStatus(Long taskId, String userEmail, TaskStatus status) {
        ProjectTask task = getTaskById(taskId);
        Project project = task.getProject();

        if (project == null) {
            throw new IllegalArgumentException("Project not found.");
        }

        boolean owner = isOwner(userEmail, project.getId());
        boolean member = isMember(userEmail, project.getId());

        if (!owner && !member) {
            throw new AccessDeniedException("Only project members can update task status.");
        }

        User actionUser = getUserByEmail(userEmail);

        task.setStatus(status);
        projectTaskRepository.save(task);

        if (project.getOwner() != null && !project.getOwner().getId().equals(actionUser.getId())) {
            notificationService.createNotification(
                    project.getOwner(),
                    "Task Status Updated",
                    actionUser.getFullName() + " updated task \"" + task.getTitle() + "\" to " + status + ".",
                    NotificationType.TASK
            );
        }

        if (task.getAssignedUser() != null && !task.getAssignedUser().getId().equals(actionUser.getId())) {
            notificationService.createNotification(
                    task.getAssignedUser(),
                    "Task Status Changed",
                    "Task \"" + task.getTitle() + "\" is now " + status + ".",
                    NotificationType.TASK
            );
        }

        return "Task status updated successfully.";
    }

    public String assignTask(Long taskId, String ownerEmail, Long userId) {
        ProjectTask task = getTaskById(taskId);
        Project project = task.getProject();

        if (project == null) {
            throw new IllegalArgumentException("Project not found.");
        }

        if (!isOwner(ownerEmail, project.getId())) {
            throw new AccessDeniedException("Only the project owner can assign tasks.");
        }

        if (userId == null) {
            task.setAssignedUser(null);
            projectTaskRepository.save(task);
            return "Task unassigned successfully.";
        }

        User assignedUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        if (!projectMemberRepository.existsByProjectAndUser(project, assignedUser)) {
            throw new IllegalArgumentException("User is not a member of this project.");
        }

        task.setAssignedUser(assignedUser);
        projectTaskRepository.save(task);

        notificationService.createNotification(
                assignedUser,
                "Task Assigned",
                "You were assigned to task: " + task.getTitle() + " in project " + project.getTitle() + ".",
                NotificationType.TASK
        );

        return "Task assigned successfully.";
    }

    public String deleteTask(Long taskId, String ownerEmail) {
        ProjectTask task = getTaskById(taskId);
        Project project = task.getProject();

        if (project == null) {
            throw new IllegalArgumentException("Project not found.");
        }

        if (!isOwner(ownerEmail, project.getId())) {
            throw new AccessDeniedException("Only the project owner can delete tasks.");
        }

        projectTaskRepository.delete(task);
        return "Task deleted successfully.";
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
    }
}