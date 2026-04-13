package com.example.GrowLink.controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.example.GrowLink.dto.ProjectTaskDto;
import com.example.GrowLink.entity.Project;
import com.example.GrowLink.enums.TaskPriority;
import com.example.GrowLink.enums.TaskStatus;
import com.example.GrowLink.service.ProjectTaskService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/projects/{projectId}/tasks")
public class ProjectTaskController {

    private final ProjectTaskService projectTaskService;

    public ProjectTaskController(ProjectTaskService projectTaskService) {
        this.projectTaskService = projectTaskService;
    }

    @GetMapping
    public String showProjectTasksPage(@PathVariable Long projectId,
                                       Model model,
                                       Principal principal,
                                       @RequestParam(value = "message", required = false) String message) {

        Project project = projectTaskService.getProjectById(projectId);

        boolean isOwner = false;
        boolean isMember = false;

        if (principal != null) {
            isOwner = projectTaskService.isOwner(principal.getName(), projectId);
            isMember = projectTaskService.isMember(principal.getName(), projectId);
        }

        if (!isOwner && !isMember) {
            return "redirect:/projects/" + projectId + "?message=" +
                    URLEncoder.encode("You do not have access to the task board.", StandardCharsets.UTF_8);
        }

        model.addAttribute("project", project);
        model.addAttribute("tasks", projectTaskService.getTasksByProjectId(projectId));
        model.addAttribute("members", projectTaskService.getProjectMembers(projectId));
        model.addAttribute("taskDto", new ProjectTaskDto());
        model.addAttribute("statuses", TaskStatus.values());
        model.addAttribute("priorities", TaskPriority.values());
        model.addAttribute("isOwner", isOwner);
        model.addAttribute("isMember", isMember);
        model.addAttribute("todoCount", projectTaskService.getTodoCount(projectId));
        model.addAttribute("inProgressCount", projectTaskService.getInProgressCount(projectId));
        model.addAttribute("doneCount", projectTaskService.getDoneCount(projectId));
        model.addAttribute("message", message);

        return "projects/project-tasks";
    }

    @PostMapping("/create")
    public String createTask(@PathVariable Long projectId,
                             @Valid @ModelAttribute("taskDto") ProjectTaskDto taskDto,
                             BindingResult bindingResult,
                             Principal principal,
                             Model model) {

        if (bindingResult.hasErrors()) {
            Project project = projectTaskService.getProjectById(projectId);

            model.addAttribute("project", project);
            model.addAttribute("tasks", projectTaskService.getTasksByProjectId(projectId));
            model.addAttribute("members", projectTaskService.getProjectMembers(projectId));
            model.addAttribute("statuses", TaskStatus.values());
            model.addAttribute("priorities", TaskPriority.values());
            model.addAttribute("isOwner", projectTaskService.isOwner(principal.getName(), projectId));
            model.addAttribute("isMember", projectTaskService.isMember(principal.getName(), projectId));
            model.addAttribute("todoCount", projectTaskService.getTodoCount(projectId));
            model.addAttribute("inProgressCount", projectTaskService.getInProgressCount(projectId));
            model.addAttribute("doneCount", projectTaskService.getDoneCount(projectId));
            return "projects/project-tasks";
        }

        String message = projectTaskService.createTask(projectId, principal.getName(), taskDto);

        return "redirect:/projects/" + projectId + "/tasks?message=" +
                URLEncoder.encode(message, StandardCharsets.UTF_8);
    }

    @PostMapping("/{taskId}/status")
    public String updateTaskStatus(@PathVariable Long projectId,
                                   @PathVariable Long taskId,
                                   @RequestParam("status") TaskStatus status,
                                   Principal principal) {

        String message = projectTaskService.updateTaskStatus(taskId, principal.getName(), status);

        return "redirect:/projects/" + projectId + "/tasks?message=" +
                URLEncoder.encode(message, StandardCharsets.UTF_8);
    }

    @PostMapping("/{taskId}/assign")
    public String assignTask(@PathVariable Long projectId,
                             @PathVariable Long taskId,
                             @RequestParam(value = "userId", required = false) Long userId,
                             Principal principal) {

        String message = projectTaskService.assignTask(taskId, principal.getName(), userId);

        return "redirect:/projects/" + projectId + "/tasks?message=" +
                URLEncoder.encode(message, StandardCharsets.UTF_8);
    }

    @PostMapping("/{taskId}/delete")
    public String deleteTask(@PathVariable Long projectId,
                             @PathVariable Long taskId,
                             Principal principal) {

        String message = projectTaskService.deleteTask(taskId, principal.getName());

        return "redirect:/projects/" + projectId + "/tasks?message=" +
                URLEncoder.encode(message, StandardCharsets.UTF_8);
    }
}