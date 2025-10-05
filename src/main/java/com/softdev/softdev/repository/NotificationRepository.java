package com.softdev.softdev.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.softdev.softdev.entity.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByIsActive(Boolean isActive);
    List<Notification> findByUser_UserIdAndIsActive(Long userId, Boolean isActive);
}
