package com.softdev.softdev.service;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.softdev.softdev.dto.bus_driver.BusStatusDTO;
import com.softdev.softdev.entity.Bus;
import com.softdev.softdev.entity.BusDriver;
import com.softdev.softdev.entity.Notification;
import com.softdev.softdev.entity.User;
import com.softdev.softdev.exception.ResourceNotFoundException;
import com.softdev.softdev.repository.BusDriverRepositiory;
import com.softdev.softdev.repository.NotificationRepository;
import com.softdev.softdev.repository.UserRepository;

import jakarta.transaction.Transactional;
import net.minidev.json.parser.ParseException;

@Service
public class BusDriverService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private BusService busService;

    @Autowired
    private UserService userService;

    @Autowired
    private BusScheduleService busScheduleService;

    @Autowired
    private BusStopETAService BusStopETAService;

    @Autowired
    private BusDriverRepositiory busDriverRepositiory;


    public  boolean isBusDriver(Long userId) {
        return userRepository.findById(userId).map(user -> "BUS_DRIVER".equals(user.getRole())).orElse(false);
    }

    public Map<String, Object> getDrivingStatus(Long userId) throws ParseException{
        if (isBusDriver(userId)){
            long busid = busDriverRepositiory.findByUserId(userId).getBusId();
            Map<String, Object> position = busService.showBusPosition(busid);
            long nextStopId = (long) position.get("nextStop");
            if (nextStopId == 0){
                return Map.of(
                    "latitude", position.get("latitude"),
                    "longitude", position.get("longitude"),
                    "isStopped", position.get("isStopped"),
                    "status", DrivingStatus.REST,
                    "difference_seconds", 0
                );
            }
            Map<String, Object> ETAToNextStop = BusStopETAService.ETAToStop(busid, (long) position.get("nextStop"));
            double ETA = (double) ETAToNextStop.get("eta_seconds");
            LocalTime schedule_time = busScheduleService.findBusScheduleTime(busid, (long) position.get("nextStop"), (Integer) position.get("currentRound"));
            Map<String, Object> status = compareSchedule(schedule_time, ETA);
            
            return Map.of(
                "latitude", position.get("latitude"),
                "longitude", position.get("longitude"),
                "isStopped", position.get("isStopped"),
                "status", status.get("status"),
                "difference_seconds", status.get("difference_seconds")
            );
        }
        throw new RuntimeException("Unauthorized request");
    }

    public Map<String, Object> compareSchedule(LocalTime schedule_time, double ETA){
        LocalTime currentTime = LocalTime.now();
        LocalTime estimateArrivalTime = currentTime.plusSeconds((long) ETA);
        if (estimateArrivalTime.isAfter(schedule_time.plusSeconds(30))){
            return Map.of(
                "status", DrivingStatus.LATE,
                "difference_seconds", java.time.Duration.between(schedule_time, estimateArrivalTime).getSeconds()
            );
        } else if (estimateArrivalTime.isBefore(schedule_time.minusSeconds(30))){
            return Map.of(
                "status", DrivingStatus.EARLY,
                "difference_seconds", java.time.Duration.between(estimateArrivalTime, schedule_time).getSeconds()
            );
        } else {
            return Map.of(
                "status", DrivingStatus.ONTIME,
                "difference_seconds", 0
            );
        }
    }

    public Bus getBusByBusDriver(User user){
        Long userId = user.getUserId();
        BusDriver busDriver = busDriverRepositiory.findByUserId(userId);
        
        return busService.getBusById(busDriver.getBusId());
    }

    @Transactional
    public Integer sendEmergenyNotification(String token) {
        User user = userService.getCurrentUser(token);
        Bus bus = getBusByBusDriver(user);
        if (bus == null){
            throw new ResourceNotFoundException("Cannot find Bus by this User");
        }

        List<Notification> byBus = notificationRepository.findByBus(bus); // one query
        LocalTime now = LocalTime.now();

        List<Notification> emergencies = byBus.stream()
            .filter(n -> Boolean.FALSE.equals(n.getIsActive())
                || (now.isBefore(n.getScheduleTime()) && now.isAfter(n.getTimeToSend())))
            .map(n -> {
                Notification em = new Notification();
                em.setTitle("Emergency Notification from Bus " + bus.getBusId());
                em.setMessage("Sorry, the bus cannot operate at the moment due to an emergency.");
                em.setBus(bus);
                em.setTimeToSend(now);
                em.setScheduleTime(now);
                em.setUser(n.getUser());      
                em.setBusStop(n.getBusStop());          
                em.setIsActive(true);

                notificationRepository.delete(n);

                return em;
            })
            .toList();

        if (emergencies.isEmpty()) return 0;

        notificationRepository.saveAll(emergencies);
        return emergencies.size();
    }


    public BusStatusDTO toDto(Map<String, Object> status) {
        BusStatusDTO dto = new BusStatusDTO();
        dto.setLatitude((Double) status.get("latitude"));
        dto.setLongitude((Double) status.get("longitude"));
        dto.setStopped((Boolean) status.get("isStopped"));
        dto.setStatus((Enum<DrivingStatus>) status.get("status"));
        dto.setDifference_seconds((Long) status.get("difference_seconds"));
        return dto;
    }

    public enum DrivingStatus{
        LATE,
        ONTIME,
        EARLY,
        REST
    }
}
