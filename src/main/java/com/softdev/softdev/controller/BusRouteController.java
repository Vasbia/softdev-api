package com.softdev.softdev.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.softdev.softdev.dto.bus.BusDTO;
import com.softdev.softdev.dto.route.RouteDTO;
import com.softdev.softdev.dto.routepath.RoutePathDTO;
import com.softdev.softdev.entity.Bus;
import com.softdev.softdev.entity.Route;
import com.softdev.softdev.repository.RouteRepository;
import com.softdev.softdev.service.BusService;
import com.softdev.softdev.service.RoutePathService;
import com.softdev.softdev.service.RouteService;

@RestController
@RequestMapping("/api/busroute")
public class BusRouteController {
    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private RoutePathService routePathService;

    @Autowired
    private RouteService routeService;

    @Autowired
    private BusService busService;

    @GetMapping("/all")
    public List<RouteDTO> getAllRoute() {
        List<Route> routes = routeRepository.findAll();
        return routeService.toDtos(routes);
    }

    @GetMapping("/path/{routeId}")
    public List<RoutePathDTO> getRoutePathById(@PathVariable Long routeId) {
        return routePathService.toDtos(routePathService.findRoutePathByRouteId(routeId));
    }

    @GetMapping("/activebus/{routeId}")
    public List<BusDTO> getActiveBusesByRouteId(@PathVariable Long routeId) {
        List<Bus> allBus = busService.findAllByRouteId(routeId);
        List<Bus> activeBus = allBus.stream().filter(bus -> busService.isActive(bus.getBusId())).toList();
        return busService.toDtos(activeBus);
    }
}
