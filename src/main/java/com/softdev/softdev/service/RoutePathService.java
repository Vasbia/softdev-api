package com.softdev.softdev.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.softdev.softdev.entity.RoutePath;
import com.softdev.softdev.exception.ResourceNotFoundException;
import com.softdev.softdev.repository.RoutePathRepository;

@Service
public class RoutePathService {
    @Autowired
    private RoutePathRepository routePathRepository;

    @Autowired
    private GeolocationService geolocationService;

    public List<RoutePath> findRoutePathByRouteId(Long routeId) {
        return routePathRepository.findAllByRouteRouteId(routeId).orElseThrow(() -> new ResourceNotFoundException("Route paths not found for routeId: " + routeId));
    }

    public List<Double> getCumulativeDistance(List<RoutePath> routePaths) {
        List<Double> cumulative = new ArrayList<>();
        cumulative.add(0.0);
        
        double totalDistance = 0.0;

        for (int i = 0; i < routePaths.size() - 1; i++) {
            double distance = geolocationService.haversine(
                    routePaths.get(i).getGeoLocation().getLatitude(),
                    routePaths.get(i).getGeoLocation().getLongitude(),
                    routePaths.get(i + 1).getGeoLocation().getLatitude(),
                    routePaths.get(i + 1).getGeoLocation().getLongitude()
            );

            totalDistance += distance;
            
            cumulative.add(totalDistance);
        }

        return cumulative;
    } 
}
