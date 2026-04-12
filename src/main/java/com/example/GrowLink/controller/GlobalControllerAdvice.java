package com.example.GrowLink.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.example.GrowLink.entity.User;
import com.example.GrowLink.service.MessageService;
import com.example.GrowLink.service.NotificationService;
import com.example.GrowLink.service.UserService;

@ControllerAdvice
public class GlobalControllerAdvice {

    private final UserService userService;
    private final NotificationService notificationService;
    private final MessageService messageService;

    public GlobalControllerAdvice(UserService userService,
                                  NotificationService notificationService,
                                  MessageService messageService) {
        this.userService = userService;
        this.notificationService = notificationService;
        this.messageService = messageService;
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

    @ModelAttribute("unreadMessageCount")
    public long unreadMessageCount(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return 0;
        }

        return messageService.getTotalUnreadMessageCount(authentication.getName());
    }

    @ModelAttribute("loggedUser")
    public User loggedUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        return userService.getUserByEmail(authentication.getName());
    }
}