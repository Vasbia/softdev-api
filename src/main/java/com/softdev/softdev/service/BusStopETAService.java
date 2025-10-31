package com.softdev.softdev.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.softdev.softdev.dto.busstop.BusStopETADTO;
import com.softdev.softdev.entity.Bus;
import com.softdev.softdev.entity.BusSchedule;
import com.softdev.softdev.entity.BusStop;
import com.softdev.softdev.entity.RoutePath;
import com.softdev.softdev.exception.ResourceNotFoundException;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

@Service
public class BusStopETAService {
    @Autowired
    private BusService BusService;

    @Autowired
    private BusStopService busStopService;

    @Autowired
    private RoutePathService routePathService;

    @Autowired
    private GeolocationService geolocationService;

    @Autowired
    private BusScheduleService busScheduleService;

    @Autowired
    private BusService busService;

    public BusStopETADTO toDto(Map<String, Object> busStopETA) {
        BusStopETADTO dto = new BusStopETADTO();
        dto.setBus_id((Long) busStopETA.get("bus_id"));
        dto.setStop_id((Long) busStopETA.get("stop_id"));
        dto.setEta_seconds(((double) busStopETA.get("eta_seconds")));
        return dto;
    }

    public List<BusStopETADTO> toDtos(List<Map<String, Object>> busStopETAs) {
        return busStopETAs.stream().map(this::toDto).toList();
    }

    public Map<String, Object> ETAToStop(Long busId, Long stopId) throws ParseException {
        // double BUS_SPEED_KMH = 10.0;
        // double BUS_SPEED_MS = BUS_SPEED_KMH * 1000 / 3600;

        // Bus bus = BusService.getBusById(busId);
        // List<RoutePath> routePaths = routePathService.findRoutePathByRouteId(bus.getRoute().getRouteId());
        // List<Double> cumulative = routePathService.getCumulativeDistance(routePaths);

        // Map<String, Object> pos = BusService.showBusPosition(busId);
        // double busLat = (double) pos.get("latitude");
        // double busLon = (double) pos.get("longitude");

        // double minDist = Double.MAX_VALUE;
        // int nearestIndex = 0;
        // for (int i = 0; i < routePaths.size(); i++) {
        //     double lat = routePaths.get(i).getGeoLocation().getLatitude();
        //     double lon = routePaths.get(i).getGeoLocation().getLongitude();
        //     double d = geolocationService.haversine(busLat, busLon, lat, lon);
        //     if (d < minDist) {
        //         minDist = d;
        //         nearestIndex = i;
        //     }
        // }
        // double busCumulativeDist = cumulative.get(nearestIndex);

        // List<Double> busStopDistances = busStopService.getBusStopDistances(bus.getRoute().getRouteId(), routePaths, cumulative);
        // double stopCumulativeDist = busStopDistances.get(4);

        // double remainingDist = stopCumulativeDist - busCumulativeDist;
        // if (remainingDist < 0) {
        //     remainingDist = 0;
        // }
        
        // double timeToReachStop = remainingDist / BUS_SPEED_MS;

        // return Map.of(
        //     "bus_id", busId,
        //     "stop_id", stopId,
        //     "eta_seconds", timeToReachStop
        // );

        // Get bus info and schedules
        Bus bus = BusService.getBusById(busId);
        List<BusSchedule> busSchedules = busScheduleService.findBusScheduleByBusId(busId);
        if (busSchedules.isEmpty()) {
            throw new ResourceNotFoundException("No bus schedules found for busId: " + busId);
        }

        // Determine active round
        Integer currentRound = busService.getCurrentRound(busId);
        if (currentRound == null) {
            throw new ResourceNotFoundException("No active round found for busId: " + busId);
        }

        // Sort schedules for this round
        List<BusSchedule> roundSchedules = busSchedules.stream()
                .filter(s -> s.getRound().equals(currentRound))
                .sorted(Comparator.comparing(BusSchedule::getScheduleOrder))
                .collect(Collectors.toList());

        // Get simulated current position (from your ideal logic)
        Map<String, Object> pos = busService.showBusPosition(busId);
        Long nextStopId = (Long) pos.get("nextStop");

        // --- Find the nextStopId occurrence that matches the bus's current segment ---
        int nextStopIndex = -1;
        LocalTime now = LocalTime.now();

        // Find the *closest* next stop occurrence that has a future scheduled arrival time
        for (int i = 0; i < roundSchedules.size(); i++) {
            BusSchedule s = roundSchedules.get(i);
            if (s.getBusStop().getBusStopId().equals(nextStopId)
                    && (s.getArriveTime().isAfter(now) || s.getArriveTime().equals(now))) {
                nextStopIndex = i;
                break;
            }
        }

        // If still not found, fallback to last occurrence
        if (nextStopIndex == -1) {
            for (int i = roundSchedules.size() - 1; i >= 0; i--) {
                if (roundSchedules.get(i).getBusStop().getBusStopId().equals(nextStopId)) {
                    nextStopIndex = i;
                    break;
                }
            }
        }

        if (nextStopIndex == -1) {
            throw new ResourceNotFoundException("Next stop not found in schedule for busId: " + busId);
        }

        // --- Find the *target stop occurrence* AFTER the nextStopIndex ---
        int targetStopIndex = -1;
        for (int i = nextStopIndex; i < roundSchedules.size(); i++) {
            if (roundSchedules.get(i).getBusStop().getBusStopId().equals(stopId)) {
                targetStopIndex = i;
                break;
            }
        }

        // If still not found, target stop is before or not in current round
        if (targetStopIndex == -1) {
            return Map.of(
                "bus_id", busId,
                "stop_id", stopId,
                "eta_seconds", (double) 0.0
            );
        }

        BusSchedule targetSchedule = roundSchedules.get(targetStopIndex);
        LocalTime targetArriveTime = targetSchedule.getArriveTime();

        Double etaSeconds = (double) Duration.between(now, targetArriveTime).toSeconds();
        if (etaSeconds < 0) etaSeconds = (double) 0.0;

        return Map.of(
            "bus_id", busId,
            "stop_id", stopId,
            "eta_seconds", etaSeconds
        );

    }

    public List<Map<String, Object>> ETAToAllStop(Long busId) {
        Long busrouteid = BusService.getBusById(busId).getRoute().getRouteId();
        List<BusStop> stops = busStopService.findAllByRouteRouteId(busrouteid);
        List<Map<String, Object>> etas = stops.stream().map(stop -> {
            try {
                return ETAToStop(busId, stop.getBusStopId());
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }).toList();
        return etas;
    }
}
