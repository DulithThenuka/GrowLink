package com.example.GrowLink.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.GrowLink.entity.Notification;
import com.example.GrowLink.entity.User;
import com.example.GrowLink.enums.NotificationType;
import com.example.GrowLink.repository.NotificationRepository;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserService userService;

    public NotificationService(NotificationRepository notificationRepository,
                               UserService userService) {
        this.notificationRepository = notificationRepository;
        this.userService = userService;
    }

    @Transactional
    public void createNotification(User user, String title, String message, NotificationType type) {
        if (user == null) {
            return;
        }

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setIsRead(false);
        notification.setCreatedAt(LocalDateTime.now());

        notificationRepository.save(notification);
    }

    public List<Notification> getNotificationsForUser(User user) {
        if (user == null) {
            return List.of();
        }
        return notificationRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public List<Notification> getUnreadNotifications(User user) {
        if (user == null) {
            return List.of();
        }
        return notificationRepository.findByUserAndIsReadFalseOrderByCreatedAtDesc(user);
    }

    public long getUnreadCount(User user) {
        if (user == null) {
            return 0;
        }
        return notificationRepository.countByUserAndIsReadFalse(user);
    }

    public long getUnreadCountByUserEmail(String email) {
        if (email == null || email.isBlank()) {
            return 0;
        }

        User user = userService.getUserByEmail(email);
        return getUnreadCount(user);
    }

    @Transactional
    public void markAsRead(Long notificationId, User user) {
        Notification notification = notificationRepository.findById(notificationId).orElse(null);

        if (notification == null || user == null) {
            return;
        }

        if (!notification.getUser().getId().equals(user.getId())) {
            return;
        }

        if (Boolean.TRUE.equals(notification.getIsRead())) {
            return;
        }

        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    @Transactional
    public void markAllAsRead(User user) {
        if (user == null) {
            return;
        }

        List<Notification> unreadNotifications =
                notificationRepository.findByUserAndIsReadFalseOrderByCreatedAtDesc(user);

        for (Notification notification : unreadNotifications) {
            notification.setIsRead(true);
        }

        notificationRepository.saveAll(unreadNotifications);
    }
}