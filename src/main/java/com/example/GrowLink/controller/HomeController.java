package com.example.GrowLink.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.GrowLink.service.ConnectionService;
import com.example.GrowLink.service.LearningRequestService;
import com.example.GrowLink.service.NotificationService;
import com.example.GrowLink.service.SkillService;
import java.util.Collections;

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

    if (principal == null) {
        return "redirect:/login";
    }

    String email = principal.getName();

    model.addAttribute("teachSkillCount",
            skillService.getTeachSkillsByUserEmail(email).size());

    model.addAttribute("learnSkillCount",
            skillService.getLearnSkillsByUserEmail(email).size());

    // ⚠ FIXED NAME (was connectionsCount)
    model.addAttribute("connectionCount",
            connectionService.getAcceptedConnections(email).size());

    // These are REQUIRED by your HTML (you were missing them!)
    model.addAttribute("sentLearningCount",
            learningRequestService.getSentRequests(email).size());

    model.addAttribute("receivedLearningCount",
            learningRequestService.getReceivedRequests(email).size());

    model.addAttribute("unreadNotificationCount",
            notificationService.getUnreadCount(email));

    // VERY IMPORTANT (avoid crash if not implemented yet)
model.addAttribute("recommendedProjects", Collections.emptyList());
model.addAttribute("recommendedUsers", Collections.emptyList());

    return "dashboard";
}

}