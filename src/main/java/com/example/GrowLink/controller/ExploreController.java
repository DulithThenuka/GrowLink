package com.example.GrowLink.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.GrowLink.entity.Project;
import com.example.GrowLink.entity.User;
import com.example.GrowLink.repository.ProjectRepository;
import com.example.GrowLink.repository.UserRepository;

@Controller
@RequestMapping("/explore")
public class ExploreController {

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    public ExploreController(UserRepository userRepository,
                             ProjectRepository projectRepository) {
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
    }

    // ================= USERS =================
    @GetMapping("/users")
    public String exploreUsers(
            @RequestParam(required = false) String keyword,
            Model model
    ) {
        List<User> users;

        if (keyword != null && !keyword.isEmpty()) {
            users = userRepository.findByFullNameContainingIgnoreCase(keyword);
        } else {
            users = userRepository.findAll();
        }

        model.addAttribute("users", users);
        model.addAttribute("keyword", keyword);

        return "explore/users";
    }

    // ================= PROJECTS =================
    @GetMapping("/projects")
    public String exploreProjects(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            Model model
    ) {
        List<Project> projects;

        if (keyword != null && !keyword.isEmpty()) {
            projects = projectRepository.findByTitleContainingIgnoreCase(keyword);
        } else if (category != null && !category.isEmpty()) {
            projects = projectRepository.findByCategoryContainingIgnoreCase(category);
        } else {
            projects = projectRepository.findAll();
        }

        model.addAttribute("projects", projects);
        model.addAttribute("keyword", keyword);
        model.addAttribute("category", category);

        return "explore/projects";
    }
}