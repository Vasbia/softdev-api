package com.softdev.softdev.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.softdev.softdev.dto.APIResponseDTO;
import com.softdev.softdev.dto.busstop.BusScheduleOfBusStopDTO;
import com.softdev.softdev.dto.busstop.BusStopDTO;
import com.softdev.softdev.dto.busstop.BusStopETADTO;
import com.softdev.softdev.dto.notification.NotificationDTO;
import com.softdev.softdev.entity.BusStop;
import com.softdev.softdev.service.BusStopETAService;
import com.softdev.softdev.service.BusStopService;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping("/api/busstop")
public class BusStopController {
    @Autowired
    private BusStopService busStopService;

    @Autowired
    private BusStopETAService busStopETAService;

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

    @GetMapping("/eta/{busId}/{stopId}")
    public BusStopETADTO getStopETA(@PathVariable Long busId, @PathVariable Long stopId) throws Exception {
        Map<String, Object> busStopETA = busStopETAService.ETAToStop(busId, stopId);
        return busStopETAService.toDto(busStopETA);
    }

    @GetMapping("/eta/{busId}/all")
    public List<BusStopETADTO> getStopETA(@PathVariable Long busId) {
        List<Map<String, Object>> allBusStopETA = busStopETAService.ETAToAllStop(busId);
        return busStopETAService.toDtos(allBusStopETA);
    }

    @GetMapping("/getBusShedule")
    public BusScheduleOfBusStopDTO getBusShedule(Long busId) {

       return busStopService.getBusArriveTimeSchedule(busId);
       
    }
    
}
