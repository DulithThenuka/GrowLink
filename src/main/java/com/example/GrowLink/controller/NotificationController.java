package com.example.GrowLink.controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.GrowLink.service.NotificationService;

@Controller
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public String showNotificationsPage(Model model,
                                        Principal principal,
                                        @RequestParam(value = "message", required = false) String message) {

        if (principal == null) {
            return "redirect:/login";
        }

        model.addAttribute("notifications", notificationService.getNotificationsByUserEmail(principal.getName()));
        model.addAttribute("unreadCount", notificationService.getUnreadCount(principal.getName()));
        model.addAttribute("message", message);

        return "notifications/list";
    }

    @PostMapping("/{notificationId}/read")
    public String markAsRead(@PathVariable Long notificationId, Principal principal) {
        String message = notificationService.markAsRead(notificationId, principal.getName());

        return "redirect:/notifications?message=" +
                URLEncoder.encode(message, StandardCharsets.UTF_8);
    }

    @PostMapping("/read-all")
    public String markAllAsRead(Principal principal) {
        String message = notificationService.markAllAsRead(principal.getName());

        return "redirect:/notifications?message=" +
                URLEncoder.encode(message, StandardCharsets.UTF_8);
    }
}