package com.softdev.softdev.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.softdev.softdev.service.BusStopETAService;

@RestController
@RequestMapping("/api/busstop")
public class BusStopController {
    @Autowired
    private BusStopETAService busStopETAService;

    @GetMapping("/eta/{stopId}")
    public Map<String, Object> getStopETA(@PathVariable Long stopId, Double buslat, Double buslon) throws Exception {
        return busStopETAService.ETAToStop(buslat, buslon, stopId);
    }
}
