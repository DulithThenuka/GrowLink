package com.example.GrowLink.controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.GrowLink.dto.ProjectAnalyticsDto;
import com.example.GrowLink.entity.Project;
import com.example.GrowLink.service.ProjectAnalyticsService;

@Controller
@RequestMapping("/projects/{projectId}/analytics")
public class ProjectAnalyticsController {

    private final ProjectAnalyticsService projectAnalyticsService;

    public ProjectAnalyticsController(ProjectAnalyticsService projectAnalyticsService) {
        this.projectAnalyticsService = projectAnalyticsService;
    }

    @GetMapping
    public String showProjectAnalyticsPage(@PathVariable Long projectId,
                                           Model model,
                                           Principal principal,
                                           @RequestParam(value = "message", required = false) String message) {

        if (principal == null) {
            return "redirect:/login";
        }

        Project project = projectAnalyticsService.getProjectById(projectId);

        boolean isOwner = projectAnalyticsService.isOwner(principal.getName(), projectId);
        boolean isMember = projectAnalyticsService.isMember(principal.getName(), projectId);

        if (!isOwner && !isMember) {
            return "redirect:/projects/" + projectId + "?message=" +
                    URLEncoder.encode("You do not have access to project analytics.", StandardCharsets.UTF_8);
        }

        ProjectAnalyticsDto analytics = projectAnalyticsService.getAnalytics(projectId, principal.getName());

        model.addAttribute("project", project);
        model.addAttribute("analytics", analytics);
        model.addAttribute("isOwner", isOwner);
        model.addAttribute("isMember", isMember);
        model.addAttribute("message", message);

        return "projects/project-analytics";
    }
}