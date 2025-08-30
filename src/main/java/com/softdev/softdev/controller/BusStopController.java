package com.softdev.softdev.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.softdev.softdev.dto.busstop.BusStopDTO;
import com.softdev.softdev.entity.BusStop;
import com.softdev.softdev.service.BusStopService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/api/busstop")
public class BusStopController {
    @Autowired
    private BusStopService busStopService;

    @GetMapping("route/{routeId}")
    public List<BusStopDTO> getBusStopByRouteId(@PathVariable Long routeId) {
        List<BusStop> busStops = busStopService.findAllByRouteRouteId(routeId);
        return busStopService.toDtos(busStops);
    }

}
