package com.example.GrowLink.controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.example.GrowLink.dto.ProjectDto;
import com.example.GrowLink.entity.Project;
import com.example.GrowLink.enums.ProjectStatus;
import com.example.GrowLink.service.ProjectService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public String showProjectsPage(Model model,
                                   Principal principal,
                                   @RequestParam(value = "message", required = false) String message,
                                   @RequestParam(value = "keyword", required = false) String keyword,
                                   @RequestParam(value = "category", required = false) String category) {

        model.addAttribute("allProjects", projectService.searchProjects(keyword, category));

        if (principal != null) {
            model.addAttribute("myProjects", projectService.getProjectsByOwnerEmail(principal.getName()));
            model.addAttribute("joinedProjects", projectService.getProjectsJoinedByUserEmail(principal.getName()));
        }

        model.addAttribute("message", message);
        model.addAttribute("keyword", keyword);
        model.addAttribute("category", category);

        return "projects/projects";
    }

    @GetMapping("/create")
    public String showCreateProjectPage(Model model) {
        model.addAttribute("projectDto", new ProjectDto());
        return "projects/create-project";
    }

    @PostMapping("/create")
    public String createProject(@Valid @ModelAttribute("projectDto") ProjectDto projectDto,
                                BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "projects/create-project";
        }

        return "projects/create-project";
    }

    @PostMapping("/save")
    public String saveProject(@Valid @ModelAttribute("projectDto") ProjectDto projectDto,
                              BindingResult bindingResult,
                              Principal principal) {

        if (bindingResult.hasErrors()) {
            return "projects/create-project";
        }

        projectService.createProject(principal.getName(), projectDto);
        return "redirect:/projects?message=" + URLEncoder.encode("Project created successfully.", StandardCharsets.UTF_8);
    }

    @GetMapping("/{projectId}")
    public String showProjectDetails(@PathVariable Long projectId,
                                     Model model,
                                     Principal principal,
                                     @RequestParam(value = "message", required = false) String message) {

        Project project = projectService.getProjectById(projectId);

        boolean isOwner = false;
        boolean isMember = false;
        boolean hasPendingJoinRequest = false;

        if (principal != null) {
            isOwner = projectService.isOwner(principal.getName(), projectId);
            isMember = projectService.isMember(principal.getName(), projectId);
            hasPendingJoinRequest = projectService.hasPendingJoinRequest(principal.getName(), projectId);
        }

        model.addAttribute("project", project);
        model.addAttribute("members", projectService.getMembersByProjectId(projectId));
        model.addAttribute("joinRequests", projectService.getJoinRequestsByProjectId(projectId));
        model.addAttribute("requiredSkills", projectService.getRequiredSkillsByProjectId(projectId));
        model.addAttribute("isOwner", isOwner);
        model.addAttribute("isMember", isMember);
        model.addAttribute("hasPendingJoinRequest", hasPendingJoinRequest);
        model.addAttribute("statuses", ProjectStatus.values());
        model.addAttribute("message", message);

        return "projects/project-details";
    }

    @PostMapping("/{projectId}/join")
    public String joinProject(@PathVariable Long projectId, Principal principal) {
        String message = projectService.sendJoinRequest(principal.getName(), projectId);
        return "redirect:/projects/" + projectId + "?message=" + URLEncoder.encode(message, StandardCharsets.UTF_8);
    }

    @PostMapping("/{projectId}/cancel-request")
    public String cancelJoinRequest(@PathVariable Long projectId, Principal principal) {
        String message = projectService.cancelJoinRequest(principal.getName(), projectId);
        return "redirect:/projects/" + projectId + "?message=" + URLEncoder.encode(message, StandardCharsets.UTF_8);
    }

    @PostMapping("/{projectId}/status")
    public String updateProjectStatus(@PathVariable Long projectId,
                                      @RequestParam("status") ProjectStatus status,
                                      Principal principal) {
        String message = projectService.updateProjectStatus(principal.getName(), projectId, status);
        return "redirect:/projects/" + projectId + "?message=" + URLEncoder.encode(message, StandardCharsets.UTF_8);
    }

    @PostMapping("/requests/{requestId}/accept")
    public String acceptJoinRequest(@PathVariable Long requestId,
                                    @RequestParam("projectId") Long projectId,
                                    Principal principal) {
        String message = projectService.acceptJoinRequest(principal.getName(), requestId);
        return "redirect:/projects/" + projectId + "?message=" + URLEncoder.encode(message, StandardCharsets.UTF_8);
    }

    @PostMapping("/requests/{requestId}/reject")
    public String rejectJoinRequest(@PathVariable Long requestId,
                                    @RequestParam("projectId") Long projectId,
                                    Principal principal) {
        String message = projectService.rejectJoinRequest(principal.getName(), requestId);
        return "redirect:/projects/" + projectId + "?message=" + URLEncoder.encode(message, StandardCharsets.UTF_8);
    }

    @PostMapping("/{projectId}/members/{memberUserId}/remove")
    public String removeMember(@PathVariable Long projectId,
                               @PathVariable Long memberUserId,
                               Principal principal) {
        String message = projectService.removeMember(principal.getName(), projectId, memberUserId);
        return "redirect:/projects/" + projectId + "?message=" + URLEncoder.encode(message, StandardCharsets.UTF_8);
    }
}