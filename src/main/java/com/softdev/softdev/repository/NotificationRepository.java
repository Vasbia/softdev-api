package com.softdev.softdev.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.softdev.softdev.entity.Bus;
import com.softdev.softdev.entity.Notification;
import com.softdev.softdev.entity.User;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByIsActive(Boolean isActive);
    List<Notification> findByUserAndIsActiveTrueAndIsReadFalse(User user);
    List<Notification> findByUserAndIsActiveTrue(User user);
    List<Notification> findByUser(User user);
    long deleteByUserAndIsActiveTrueAndIsReadTrue(User user);
    List<Notification> findByBus(Bus bus);
    void delete(Notification notification);
}
