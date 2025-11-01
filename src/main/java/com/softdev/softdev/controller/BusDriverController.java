package com.softdev.softdev.controller;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.softdev.softdev.dto.bus_driver.BusScheduleDTO;
import com.softdev.softdev.dto.bus_driver.BusStatusDTO;
import com.softdev.softdev.entity.Bus;
import com.softdev.softdev.entity.BusSchedule;
import com.softdev.softdev.entity.User;
import com.softdev.softdev.repository.BusRepository;
import com.softdev.softdev.service.BusDriverService;
import com.softdev.softdev.service.BusScheduleService;
import com.softdev.softdev.service.BusService;
import com.softdev.softdev.service.UserService;

import net.minidev.json.parser.ParseException;



@RestController
@RequestMapping("/api/busdriver")
public class BusDriverController {
    @Autowired
    private BusScheduleService busScheduleService;

    @Autowired
    private BusDriverService busDriverService;

    @Autowired
    private UserService userService;

    @Autowired
    private BusService busService;

    @Autowired
    private BusRepository busRepository;

    @GetMapping("/info")
    public Map<String, Long> getBusDriverInfo(String token) {
        User user = userService.getCurrentUser(token);
        Bus bus = busDriverService.getBusByBusDriver(user);
        return Map.of(
            "routeId", bus.getRoute().getRouteId(),
            "busId", bus.getBusId()
        );
    }

    @GetMapping("/schedule/{busId}")
    public List<BusScheduleDTO> getBusScheduleById(@PathVariable Long busId) {
        List<BusSchedule> busSchedules = busScheduleService.findBusScheduleByBusId(busId);

        return busScheduleService.toDtos(busSchedules);
    }

    @GetMapping("/schedule/token")
    public List<BusScheduleDTO> getBusScheduleByToken(String token) {
        User user = userService.getCurrentUser(token);
        Bus bus = busDriverService.getBusByBusDriver(user);
        List<BusSchedule> busSchedules = busScheduleService.findBusScheduleByBusId(bus.getBusId());
        busSchedules = busSchedules.stream()
                .filter(schedule -> Objects.equals(schedule.getRound(), busService.getCurrentRound(bus.getBusId())))
                .toList();

        return busScheduleService.toDtos(busSchedules);
    }

    @GetMapping("/status")
    public BusStatusDTO getBusStatusById(String token) throws ParseException {
        User user = userService.getCurrentUser(token);
        Map<String, Object> status = busDriverService.getDrivingStatus(user.getUserId());

        return busDriverService.toDto(status);
    }

    @PostMapping("/emergency")
    public String sendEmergencyNotification(String token) {
        User user = userService.getCurrentUser(token);
        Bus bus = busDriverService.getBusByBusDriver(user);
        if (bus.getActive() == false){
            return "Bus is already inactive.";
        }
        bus.setActive(false);
        busRepository.save(bus);
        return String.format("Send %d Emergency Notification", busDriverService.sendEmergenyNotification(token));
    }
    
    @PostMapping("/recover")
    public String recover(String token) {
        User user = userService.getCurrentUser(token);
        Bus bus = busDriverService.getBusByBusDriver(user);
        if (bus.getActive() == true){
            return "Bus is already active.";
        }
        bus.setActive(true);
        busRepository.save(bus);

        return String.format("recover busid: %d", bus.getBusId());
    }
}
