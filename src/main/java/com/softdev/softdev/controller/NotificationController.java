package com.softdev.softdev.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.softdev.softdev.dto.APIResponseDTO;
import com.softdev.softdev.dto.notification.CreateNotificationDTO;
import com.softdev.softdev.dto.notification.NotificationDTO;
import com.softdev.softdev.entity.Notification;
import com.softdev.softdev.service.NotificationService;
import com.softdev.softdev.service.UserService;

import jakarta.validation.Valid;




@RestController
@RequestMapping("/api/notification")
public class NotificationController {

    @Autowired
    private UserService userService;

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/TrackBusStop")
    public ResponseEntity<?> createNotification(
        @Valid @ModelAttribute CreateNotificationDTO createNotificationDTO
    )
    {
        
        Notification notification = notificationService.CreateNotificationTrackBusStop(
            createNotificationDTO.getBus_stop_id(),
            createNotificationDTO.getBus_id(),
            createNotificationDTO.getToken(),
            createNotificationDTO.getTimeToNotify()
        );

        NotificationDTO notificationDTO = notificationService.toDto(notification);
        APIResponseDTO<NotificationDTO> response = new APIResponseDTO<>();
        response.setData(notificationDTO);
        response.setMessage("Notification created successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("getNotification")
    public List<NotificationDTO> getAllNotification( String token ) {

        List<Notification> notifications = notificationService.getNotifications(token);

        return notificationService.toDtoList(notifications);
    }

    @GetMapping("/update")
    public ResponseEntity<?> setActiveNotification() {

        List<Notification> notifications = notificationService.setActiveNotification();
        
        APIResponseDTO<NotificationDTO> response = new APIResponseDTO<>();
        response.setMessage("Update Notification successfully.");
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/countNotification")
    public Integer countNotification(String token) {
        return notificationService.countActiveNotification(token);
    }

    @DeleteMapping("/delete/{notificationId}")
    public ResponseEntity<?> deleteNotification(Long notificationId){

        APIResponseDTO<NotificationDTO> response = new APIResponseDTO<>();
        response.setMessage(notificationService.deleteNotification(notificationId));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/deleteAllNotification")
    public ResponseEntity<?> deleteAllNotification(String token){

        APIResponseDTO<NotificationDTO> response = new APIResponseDTO<>();
        response.setMessage(notificationService.deleteAllNotification(token));
        return ResponseEntity.ok(response);
    }
    
    
    
}
