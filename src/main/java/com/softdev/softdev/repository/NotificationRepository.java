package com.softdev.softdev.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.softdev.softdev.entity.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Notification findByNotificationId(Long notificationId);
    List<Notification> findByIsActive(Boolean isActive);
    List<Notification> findByUserAndIsActive(Long userId, Boolean isActive);
}
