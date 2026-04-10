package com.example.GrowLink.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.GrowLink.entity.Notification;
import com.example.GrowLink.entity.User;
import com.example.GrowLink.service.NotificationService;
import com.example.GrowLink.service.UserService;

@Controller
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;

    public NotificationController(NotificationService notificationService,
                                  UserService userService) {
        this.notificationService = notificationService;
        this.userService = userService;
    }

    @GetMapping("/notifications")
    public String notifications(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        User loggedUser = userService.getUserByEmail(authentication.getName());

        List<Notification> notifications = notificationService.getNotificationsForUser(loggedUser);
        List<Notification> unreadNotifications = notificationService.getUnreadNotifications(loggedUser);
        long unreadCount = notificationService.getUnreadCount(loggedUser);

        model.addAttribute("loggedUser", loggedUser);
        model.addAttribute("notifications", notifications);
        model.addAttribute("unreadNotifications", unreadNotifications);
        model.addAttribute("unreadCount", unreadCount);

        return "notifications/notifications";
    }

    @PostMapping("/notifications/read")
    public String markAsRead(@RequestParam("notificationId") Long notificationId,
                             Authentication authentication,
                             RedirectAttributes redirectAttributes) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        User loggedUser = userService.getUserByEmail(authentication.getName());
        notificationService.markAsRead(notificationId, loggedUser);

        redirectAttributes.addFlashAttribute("successMessage", "Notification marked as read.");
        return "redirect:/notifications";
    }

    @PostMapping("/notifications/read-all")
    public String markAllAsRead(Authentication authentication,
                                RedirectAttributes redirectAttributes) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        User loggedUser = userService.getUserByEmail(authentication.getName());
        notificationService.markAllAsRead(loggedUser);

        redirectAttributes.addFlashAttribute("successMessage", "All notifications marked as read.");
        return "redirect:/notifications";
    }
}