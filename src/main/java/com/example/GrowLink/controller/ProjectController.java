package com.example.GrowLink.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.GrowLink.dto.ProjectDto;
import com.example.GrowLink.service.ProjectService;

@Controller
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public String projects(Model model) {
        model.addAttribute("projects", projectService.getAllProjects());
        return "projects/projects";
    }

    @GetMapping("/create")
    public String createPage(Model model) {
        model.addAttribute("projectDto", new ProjectDto());
        return "projects/create-project";
    }

    @PostMapping("/create")
    public String createProject(@ModelAttribute ProjectDto dto, Principal principal) {
        projectService.createProject(principal.getName(), dto);
        return "redirect:/projects";
    }

    @PostMapping("/join/{id}")
    public String join(@PathVariable Long id, Principal principal) {
        projectService.joinProject(principal.getName(), id);
        return "redirect:/projects";
    }
}