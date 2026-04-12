package com.example.GrowLink.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.GrowLink.enums.ProjectStatus;
import com.example.GrowLink.service.ProjectService;
import com.example.GrowLink.service.UserService;

@Controller
@RequestMapping("/explore")
public class ExploreController {

    private final UserService userService;
    private final ProjectService projectService;

    public ExploreController(UserService userService,
                             ProjectService projectService) {
        this.userService = userService;
        this.projectService = projectService;
    }

    @GetMapping("/users")
    public String exploreUsers(@RequestParam(required = false) String keyword,
                               Model model,
                               Principal principal) {

        model.addAttribute("users", userService.searchUsers(keyword));
        model.addAttribute("keyword", keyword);

        if (principal != null) {
            model.addAttribute("currentUserEmail", principal.getName());
            model.addAttribute("recommendedUsers", userService.getRecommendedUsers(principal.getName()));
        }

        return "explore/users";
    }

    @GetMapping("/projects")
    public String exploreProjects(@RequestParam(required = false) String keyword,
                                  @RequestParam(required = false) String category,
                                  @RequestParam(required = false) ProjectStatus status,
                                  Model model,
                                  Principal principal) {

        model.addAttribute("projects", projectService.searchProjects(keyword, category, status));
        model.addAttribute("keyword", keyword);
        model.addAttribute("category", category);
        model.addAttribute("status", status);
        model.addAttribute("statuses", ProjectStatus.values());

        if (principal != null) {
            model.addAttribute("recommendedProjects", projectService.getRecommendedProjects(principal.getName()));
        }

        return "explore/projects";
    }
}