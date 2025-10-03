package com.softdev.softdev.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.softdev.softdev.dto.busstop.BusStopDTO;
import com.softdev.softdev.entity.BusStop;
import com.softdev.softdev.entity.RoutePath;
import com.softdev.softdev.exception.ResourceNotFoundException;
import com.softdev.softdev.repository.BusStopRepository;

@Service
public class BusStopService {
    @Autowired
    private BusStopRepository busStopRepository;

    public BusStop getBusStopById(Long busStopId) {
        return busStopRepository.findById(busStopId).orElseThrow(() -> new ResourceNotFoundException("Bus stop not found with id: " + busStopId));
    }

    public List<BusStop> findAllByRouteRouteId(Long routeId) {
        return busStopRepository.findAllByRouteRouteId(routeId).orElseThrow(() -> new ResourceNotFoundException("No bus stops found for routeId: " + routeId));
    }

    public List<Double> getBusStopDistances(Long routeId, List<RoutePath> routePaths, List<Double> cumulative) {
        List<BusStop> busStops = findAllByRouteRouteId(routeId);

        List<Double> busStopDistances = new ArrayList<>();

        for (BusStop stop : busStops) {
            double stopLat = stop.getGeoLocation().getLatitude();
            double stopLon = stop.getGeoLocation().getLongitude();

            for (int i = 0; i < routePaths.size(); i++) {
                double pathLat = routePaths.get(i).getGeoLocation().getLatitude();
                double pathLon = routePaths.get(i).getGeoLocation().getLongitude();

                if (Math.abs(pathLat - stopLat) == 0 && Math.abs(pathLon - stopLon) == 0) {
                    busStopDistances.add(cumulative.get(i));
                    break;
                }
            }
        }

        return busStopDistances;
    }

    public BusStopDTO toDto(BusStop busStop) {
        BusStopDTO dto = new BusStopDTO();
        dto.setName(busStop.getName());
        dto.setLatitude(busStop.getGeoLocation().getLatitude());
        dto.setLongitude(busStop.getGeoLocation().getLongitude());
        return dto;
    }

    public List<BusStopDTO> toDtos(List<BusStop> busStops) {
        return busStops.stream().map(this::toDto).toList();
    }
}
