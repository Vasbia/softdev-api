package com.softdev.softdev.service;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.softdev.softdev.entity.Bus;
import com.softdev.softdev.entity.Notification;
import com.softdev.softdev.entity.User;
import com.softdev.softdev.repository.NotificationRepository;

import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Service
public class NotificationService {
    @Autowired
    private UserService userService;
    
    @Autowired
    private BusService busService;

    @Autowired
    private NotificationRepository notificationRepository;


    public Notification CreateNotification(String title, String message, Long bus_id,Long target_user_id, LocalTime time_to_send ) {

        Bus bus = busService.getBusById(bus_id);
        User user = userService.getUserById(target_user_id);

        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setBus(bus); 
        notification.setTimeToSend(time_to_send);
        notification.setUser(user);
        notification.setIsActive(false);

        return notificationRepository.save(notification);
    }

    public List<Notification> setActiveNotification(){
        List<Notification> notifications = notificationRepository.findByIsActive(false);

        if (notifications == null){
            return null ;
        }

        // for ( Notification notification : notifications ) {
        //     if (notification.getTimeToSend().isBefore(LocalTime.now()) || notification.getTimeToSend().equals(LocalTime.now())) {
        //         notification.setIsActive(true);
        //         notificationRepository.save(notification);
        //     }

        // }

        LocalTime now = LocalTime.now();
        

        List<Notification> updatedNotifications = notifications.stream()
            .filter(notification -> notification.getTimeToSend().isBefore(now) 
                || notification.getTimeToSend().equals(now))
            .map(notification -> {
                notification.setIsActive(true);
                return notification;
            })
            .collect(Collectors.toList());


        return notificationRepository.saveAll(updatedNotifications);
    }

    public List<Notification> getNotifications(OAuth2User principal){
        User user = userService.getCurrentUser(principal);

        List<Notification> notifications = notificationRepository.findByUserAndIsActive(user.getUserId(), true);

        return notifications;
        
    }



}
