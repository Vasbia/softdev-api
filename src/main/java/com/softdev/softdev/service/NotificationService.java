package com.softdev.softdev.service;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.softdev.softdev.dto.notification.NotificationDTO;
import com.softdev.softdev.entity.Bus;
import com.softdev.softdev.entity.BusStop;
import com.softdev.softdev.entity.Notification;
import com.softdev.softdev.entity.User;
import com.softdev.softdev.exception.ResourceNotFoundException;
import com.softdev.softdev.exception.user.UserNotAuthenticatedException;
import com.softdev.softdev.repository.NotificationRepository;

import net.minidev.json.parser.ParseException;

@Service
public class NotificationService {
    @Autowired
    private UserService userService;
    
    @Autowired
    private BusService busService;

    @Autowired
    private BusStopETAService busStopETAService;

    @Autowired
    private NotificationRepository notificationRepository;


    @Autowired
    private BusScheduleService busScheduleService;

    @Autowired
    private BusStopService busStopService;

    @Autowired
    private BusDriverService busDriverService;


    public Notification CreateNotificationTrackBusStop( Long bus_stop_id ,Long bus_id, String token, Long before_minutes ) {

        Bus bus = busService.getBusById(bus_id);
        if (bus == null) {
            throw new ResourceNotFoundException("Bus not found for busId: " + bus_id);
        }
        User user = userService.getCurrentUser(token);
        if (user == null) {
            throw new UserNotAuthenticatedException("User is not authenticated");
        }
        BusStop busStop = busStopService.getBusStopById(bus_stop_id);
        if (busStop == null) {
            throw new ResourceNotFoundException("BusStop not found for busStopId: " + bus_stop_id);
        }

        Integer currentBusRound = busService.getCurrentRound(bus_id);
        // Integer currentBusRound = 2;

        LocalTime arriveTime = busScheduleService.findBusScheduleTime(bus_id, bus_stop_id, currentBusRound);
        // LocalTime arriveTime = LocalTime.now().plusMinutes(10);

        if (arriveTime == null) {
            throw new ResourceNotFoundException("Arrive time not found for busId: " + bus_id  + ", busStopId: " + bus_stop_id + ", round: " + currentBusRound);
        }

        LocalTime timeToSend = arriveTime.minusMinutes(before_minutes);

        Notification notification = new Notification();
        notification.setTitle("");
        notification.setMessage("");
        notification.setBus(bus); 
        notification.setTimeToSend(timeToSend);
        notification.setScheduleTime(arriveTime);
        notification.setUser(user);
        notification.setBusStop(busStop);
        notification.setIsActive(false);

        return notificationRepository.save(notification);
    }

    public List<Notification> setActiveNotification() {
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

                try{
                    LocalTime scheduleTime = notification.getScheduleTime();
                    Map<String, Object> ETA_data = busStopETAService.ETAToStop(notification.getBus().getBusId(), notification.getBusStop().getBusStopId());
                    double eta_seconds = (double) ETA_data.get("eta_seconds");

                    Map<String, Object> compareETA = busDriverService.compareSchedule(scheduleTime, eta_seconds);

                    System.out.println(compareETA.get("status"));
                    
                    notification.setIsActive(true);
                    return notification;

                } catch(ParseException e){
                    throw new ResourceNotFoundException("Something went wrong !!");
                }

            })
            .collect(Collectors.toList());


        return notificationRepository.saveAll(updatedNotifications);
    }

    public List<Notification> getNotifications(String token) {
        User user = userService.getCurrentUser(token);
        return notificationRepository.findByUser_UserIdAndIsActive(user.getUserId(), true);
    }

    public NotificationDTO toDto(Notification notification) {
        NotificationDTO dto = new NotificationDTO();
        dto.setTitle(notification.getTitle());
        dto.setMessage(notification.getMessage());
        dto.setBus_id(notification.getBus() != null ? notification.getBus().getBusId() : null);
        dto.setBus_stop_id(notification.getBusStop() != null ? notification.getBusStop().getBusStopId() : null);
        dto.setTime(notification.getTimeToSend());
        return dto;
    }

    public List<NotificationDTO> toDtoList(List<Notification> notifications) {
        return notifications.stream().map(this::toDto).toList();
    }

    public Integer countActiveNotification(String token) {
        User user = userService.getCurrentUser(token);
        List<Notification> notifications = notificationRepository.findByUser_UserIdAndIsActive(user.getUserId(), true);

        return notifications.size();
    }


}
