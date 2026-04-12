package com.example.GrowLink.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.GrowLink.service.ConnectionService;
import com.example.GrowLink.service.LearningRequestService;
import com.example.GrowLink.service.NotificationService;
import com.example.GrowLink.service.ProjectService;
import com.example.GrowLink.service.SkillService;
import com.example.GrowLink.service.UserService;

@Controller
public class HomeController {

    private final SkillService skillService;
    private final ConnectionService connectionService;
    private final LearningRequestService learningRequestService;
    private final NotificationService notificationService;
    private final ProjectService projectService;
    private final UserService userService;

    public HomeController(SkillService skillService,
                          ConnectionService connectionService,
                          LearningRequestService learningRequestService,
                          NotificationService notificationService,
                          ProjectService projectService,
                          UserService userService) {
        this.skillService = skillService;
        this.connectionService = connectionService;
        this.learningRequestService = learningRequestService;
        this.notificationService = notificationService;
        this.projectService = projectService;
        this.userService = userService;
    }

    @GetMapping("/")
    public String homePage() {
        return "index";
    }

    @GetMapping("/dashboard")
    public String dashboardPage(Model model, Principal principal) {
        String email = principal.getName();

        model.addAttribute("teachSkillCount", skillService.getTeachSkillsByUserEmail(email).size());
        model.addAttribute("learnSkillCount", skillService.getLearnSkillsByUserEmail(email).size());
        model.addAttribute("connectionCount", connectionService.getAcceptedConnections(email).size());
        model.addAttribute("sentLearningCount", learningRequestService.getSentRequests(email).size());
        model.addAttribute("receivedLearningCount", learningRequestService.getReceivedRequests(email).size());
        model.addAttribute("unreadNotificationCount", notificationService.getUnreadCountByUserEmail(email));
        model.addAttribute("recommendedProjects", projectService.getRecommendedProjects(email));
        model.addAttribute("recommendedUsers", userService.getRecommendedUsers(email));

        return "dashboard/dashboard";
    }
}