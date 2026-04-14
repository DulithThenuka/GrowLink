package com.example.GrowLink.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.GrowLink.entity.User;
import com.example.GrowLink.enums.ProjectStatus;
import com.example.GrowLink.service.ConnectionService;
import com.example.GrowLink.service.ProjectService;
import com.example.GrowLink.service.UserService;

@Controller
@RequestMapping("/explore")
public class ExploreController {

    private final UserService userService;
    private final ProjectService projectService;
    private final ConnectionService connectionService;

    public ExploreController(UserService userService,
                             ProjectService projectService,
                             ConnectionService connectionService) {
        this.userService = userService;
        this.projectService = projectService;
        this.connectionService = connectionService;
    }

    @GetMapping("/users")
    public String exploreUsers(@RequestParam(required = false) String keyword,
                               @RequestParam(value = "message", required = false) String message,
                               Model model,
                               Principal principal) {
        
                                

        model.addAttribute("users", userService.searchUsers(keyword));
        model.addAttribute("keyword", keyword);
        model.addAttribute("message", message);

        if (principal != null) {
            String currentUserEmail = principal.getName();
            model.addAttribute("currentUserEmail", currentUserEmail);
            model.addAttribute("recommendedUsers", userService.getRecommendedUsers(currentUserEmail));

            Map<Long, Boolean> connectableMap = new HashMap<>();
            Map<Long, Boolean> pendingMap = new HashMap<>();

            for (User user : userService.searchUsers(keyword)) {
                connectableMap.put(user.getId(), connectionService.canConnect(currentUserEmail, user.getId()));
                pendingMap.put(user.getId(), connectionService.isPendingBetween(currentUserEmail, user.getId()));
            }

            for (User user : userService.getRecommendedUsers(currentUserEmail)) {
                connectableMap.put(user.getId(), connectionService.canConnect(currentUserEmail, user.getId()));
                pendingMap.put(user.getId(), connectionService.isPendingBetween(currentUserEmail, user.getId()));
            }

            model.addAttribute("connectableMap", connectableMap);
            model.addAttribute("pendingMap", pendingMap);
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