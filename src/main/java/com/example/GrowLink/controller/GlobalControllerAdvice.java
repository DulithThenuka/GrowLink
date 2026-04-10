package com.example.GrowLink.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.example.GrowLink.entity.User;
import com.example.GrowLink.service.NotificationService;
import com.example.GrowLink.service.UserService;

@ControllerAdvice
public class GlobalControllerAdvice {

    private final UserService userService;
    private final NotificationService notificationService;

    public GlobalControllerAdvice(UserService userService,
                                  NotificationService notificationService) {
        this.userService = userService;
        this.notificationService = notificationService;
    }

    @ModelAttribute("unreadNotificationCount")
    public long unreadNotificationCount(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return 0;
        }

        User user = userService.getUserByEmail(authentication.getName());
        if (user == null) {
            return 0;
        }

        return notificationService.getUnreadCount(user);
    }

    @ModelAttribute("loggedUser")
    public User loggedUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        return userService.getUserByEmail(authentication.getName());
    }
}