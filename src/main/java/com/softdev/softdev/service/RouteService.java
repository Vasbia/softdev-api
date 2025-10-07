package com.softdev.softdev.service;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.softdev.softdev.dto.route.RouteDTO;
import com.softdev.softdev.entity.Route;

@Service
public class RouteService {
    @Autowired
    private RoutePathService routePathService;

    public RouteDTO toDto(Route route) {
        RouteDTO dto = new RouteDTO();
        dto.setRouteId(route.getRouteId());
        dto.setName(route.getName());
        dto.setDescription(route.getDescription());
        dto.setColor(route.getColorCode());
        dto.setPath(routePathService.toDtos(routePathService.findRoutePathByRouteId(route.getRouteId())));
        return dto;
    }

    public List<RouteDTO> toDtos(List<Route> routes) {
        return routes.stream().map(this::toDto).toList();
    }
}
