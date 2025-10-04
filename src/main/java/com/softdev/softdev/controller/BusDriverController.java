package com.softdev.softdev.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.softdev.softdev.dto.bus_driver.BusScheduleDTO;
import com.softdev.softdev.dto.bus_driver.BusStatusDTO;
import com.softdev.softdev.entity.BusSchedule;
import com.softdev.softdev.service.BusDriverService;
import com.softdev.softdev.service.BusScheduleService;

import net.minidev.json.parser.ParseException;


@RestController
@RequestMapping("/api/busdriver")
public class BusDriverController {
    @Autowired
    private BusScheduleService busScheduleService;

    @Autowired
    private BusDriverService busDriverService;

    @GetMapping("/schedule/{busId}")
    public List<BusScheduleDTO> getBusScheduleById(@PathVariable Long busId) {
        List<BusSchedule> busSchedules = busScheduleService.findBusScheduleByBusId(busId);

        return busScheduleService.toDtos(busSchedules);
    }

    @GetMapping("/status/{userId}")
    public BusStatusDTO getBusStatusById(@PathVariable Long userId) throws ParseException {
        Map<String, Object> status = busDriverService.getDrivingStatus(userId);

        return busDriverService.toDto(status);
    }
}
