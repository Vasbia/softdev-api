package com.softdev.softdev.controller;

import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.softdev.softdev.service.NotificationService;
import com.softdev.softdev.service.UserService;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.softdev.softdev.dto.APIResponseDTO;
import com.softdev.softdev.dto.notification.CreateNotificationDTO;
import com.softdev.softdev.dto.notification.NotificationDTO;
import com.softdev.softdev.entity.Notification;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.nimbusds.oauth2.sdk.Response;



@RestController
@RequestMapping("/api/notification")
public class NotificationController {

    @Autowired
    private UserService userService;

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/createNotification")
    public ResponseEntity<?> createNotification(
        @AuthenticationPrincipal OAuth2User principal,
        @Valid @ModelAttribute CreateNotificationDTO createNotificationDTO
    )
    {
        
        Notification notification = notificationService.CreateNotification(
            createNotificationDTO.getTitle(),
            createNotificationDTO.getMessage(),
            createNotificationDTO.getBus_stop_id(),
            createNotificationDTO.getBus_id(),
            principal
        );

        NotificationDTO notificationDTO = notificationService.toDto(notification);
        APIResponseDTO<NotificationDTO> response = new APIResponseDTO<>();
        response.setData(notificationDTO);
        response.setMessage("Notification created successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("getNotification")
    public List<NotificationDTO> getAllNotification( @AuthenticationPrincipal OAuth2User principal) {

        List<Notification> notifications = notificationService.getNotifications(principal);

        return notificationService.toDtoList(notifications);
    }

    @GetMapping("/update")
    public ResponseEntity<?> setActiveNotification() {

        List<Notification> notifications = notificationService.setActiveNotification();
        
        APIResponseDTO<NotificationDTO> response = new APIResponseDTO<>();
        response.setMessage("Update Notification successfully.");
        return ResponseEntity.ok(response);
    }
    
    
    
}
