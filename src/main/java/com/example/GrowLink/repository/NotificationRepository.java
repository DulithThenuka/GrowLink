package com.example.GrowLink.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.GrowLink.entity.Notification;
import com.example.GrowLink.entity.User;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserOrderByIdDesc(User user);

    List<Notification> findByUserAndIsReadFalseOrderByIdDesc(User user);

    long countByUserAndIsReadFalse(User user);
}