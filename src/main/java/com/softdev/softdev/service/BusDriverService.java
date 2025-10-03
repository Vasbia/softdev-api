package com.softdev.softdev.service;

import java.time.LocalTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.softdev.softdev.dto.bus_driver.BusStatusDTO;
import com.softdev.softdev.repository.UserRepository;

import net.minidev.json.parser.ParseException;

@Service
public class BusDriverService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BusService busService;

    @Autowired
    private BusScheduleService busScheduleService;

    @Autowired
    private BusStopETAService BusStopETAService;

    public  boolean isBusDriver(Long userId) {
        return userRepository.findById(userId).map(user -> "BUS_DRIVER".equals(user.getRole())).orElse(false);
    }

    public Map<String, Object> getDrivingStatus(Long userId) throws ParseException{
        if (isBusDriver(userId)){
            Long busid = 1L;
            Map<String, Object> position = busService.showBusPosition(busid);
            Map<String, Object> ETAToNextStop = BusStopETAService.ETAToStop(busid, 4L);
            Long ETA = (Long) ETAToNextStop.get("eta_seconds");
            LocalTime schedule_time = busScheduleService.findBusScheduleTime(busid, 4L, 2);
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

    public Map<String, Object> compareSchedule(LocalTime schedule_time, Long ETA){
        LocalTime currentTime = LocalTime.now();
        LocalTime estimateArrivalTime = currentTime.plusSeconds(ETA);
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
        EARLY
    }
}
