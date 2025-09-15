package com.softdev.softdev.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.softdev.softdev.dto.busstop.BusStopDTO;
import com.softdev.softdev.entity.BusStop;
import com.softdev.softdev.service.BusStopETAService;
import com.softdev.softdev.service.BusStopService;


@RestController
@RequestMapping("/api/busstop")
public class BusStopController {
    @Autowired
    private BusStopETAService busStopETAService;

    private BusStopService busStopService;

    @GetMapping("/{busStopId}")
    public BusStopDTO getBusStopById(@PathVariable Long busStopId) {
        BusStop busStop = busStopService.getBusStopById(busStopId);

        return busStopService.toDto(busStop);
    }
    
    @GetMapping("route/{routeId}")
    public List<BusStopDTO> getBusStopByRouteId(@PathVariable Long routeId) {
        List<BusStop> busStops = busStopService.findAllByRouteRouteId(routeId);

        return busStopService.toDtos(busStops);
    }

    @GetMapping("/eta/{stopId}")
    public Map<String, Object> getStopETA(@PathVariable Long stopId, Double buslat, Double buslon) throws Exception {
        return busStopETAService.ETAToStop(buslat, buslon, stopId);
    }
}
