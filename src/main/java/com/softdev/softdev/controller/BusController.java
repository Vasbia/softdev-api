package com.softdev.softdev.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.softdev.softdev.service.BusService;

@RestController
@RequestMapping("/api/bus")
public class BusController {
    @Autowired
    private BusService busService;

    @GetMapping("/position/{busId}")
    public Map<String, Object> getBusPosition(@PathVariable Long busId) {
        return busService.showBusPosition(busId);
    }
}