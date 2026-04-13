package com.example.GrowLink.service;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.GrowLink.entity.Notification;
import com.example.GrowLink.entity.User;
import com.example.GrowLink.enums.NotificationType;
import com.example.GrowLink.repository.NotificationRepository;
import com.example.GrowLink.repository.UserRepository;

@Service
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository,
                               UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    public void createNotification(User user, String title, String message, NotificationType type) {
        if (user == null) {
            return;
        }

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setRead(false);

        notificationRepository.save(notification);
    }

    @Transactional(readOnly = true)
    public List<Notification> getNotificationsByUserEmail(String email) {
        User user = getUserByEmail(email);
        return notificationRepository.findByUserOrderByIdDesc(user);
    }

    @Transactional(readOnly = true)
    public List<Notification> getUnreadNotificationsByUserEmail(String email) {
        User user = getUserByEmail(email);
        return notificationRepository.findByUserAndIsReadFalseOrderByIdDesc(user);
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(String email) {
        User user = getUserByEmail(email);
        return notificationRepository.countByUserAndIsReadFalse(user);
    }

    public String markAsRead(Long notificationId, String email) {
        User user = getUserByEmail(email);

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found."));

        if (notification.getUser() == null || !notification.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You do not have permission to update this notification.");
        }

        notification.setRead(true);
        notificationRepository.save(notification);

        return "Notification marked as read.";
    }

    public String markAllAsRead(String email) {
        User user = getUserByEmail(email);

        List<Notification> notifications = notificationRepository.findByUserAndIsReadFalseOrderByIdDesc(user);

        for (Notification notification : notifications) {
            notification.setRead(true);
        }

        notificationRepository.saveAll(notifications);

        return "All notifications marked as read.";
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
    }
}