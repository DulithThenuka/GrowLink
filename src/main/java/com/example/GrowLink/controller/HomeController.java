package com.example.GrowLink.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.GrowLink.service.ConnectionService;
import com.example.GrowLink.service.LearningRequestService;
import com.example.GrowLink.service.NotificationService;
import com.example.GrowLink.service.SkillService;

@Controller
public class HomeController {

    private final SkillService skillService;
    private final ConnectionService connectionService;
    private final LearningRequestService learningRequestService;
    private final NotificationService notificationService;

    public HomeController(SkillService skillService,
                          ConnectionService connectionService,
                          LearningRequestService learningRequestService,
                          NotificationService notificationService) {
        this.skillService = skillService;
        this.connectionService = connectionService;
        this.learningRequestService = learningRequestService;
        this.notificationService = notificationService;
    }

    @GetMapping("/")
    public String homePage(Model model, Principal principal) {
        if (principal != null) {
            model.addAttribute("unreadNotificationCount",
                    notificationService.getUnreadCount(principal.getName()));
        } else {
            model.addAttribute("unreadNotificationCount", 0);
        }
        return "index";
    }

    @GetMapping("/dashboard")
    public String dashboardPage(Model model, Principal principal) {
        String email = principal.getName();

        model.addAttribute("teachSkillCount", skillService.getTeachSkillsByUser(email).size());
        model.addAttribute("learnSkillCount", skillService.getLearnSkillsByUser(email).size());
        model.addAttribute("connectionsCount", connectionService.getAcceptedConnections(email).size());
        model.addAttribute("pendingLearningRequestsCount", learningRequestService.getReceivedRequests(email).size());
        model.addAttribute("unreadNotificationCount", notificationService.getUnreadCount(email));

        return "dashboard";
    }
}