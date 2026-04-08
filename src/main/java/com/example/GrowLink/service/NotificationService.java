package com.example.GrowLink.service;

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

    public List<Notification> getNotificationsByUserEmail(String email) {
        User user = userService.getUserByEmail(email);
        return notificationRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public long getUnreadCountByUserEmail(String email) {
        User user = userService.getUserByEmail(email);
        return notificationRepository.countByUserAndIsReadFalse(user);
    }

    @Transactional
    public void createNotification(User user, String title, String message, NotificationType type) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setIsRead(false);

        notificationRepository.save(notification);
    }
}