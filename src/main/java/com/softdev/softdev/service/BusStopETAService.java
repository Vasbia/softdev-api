package com.softdev.softdev.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.softdev.softdev.dto.busstop.BusStopETADTO;
import com.softdev.softdev.entity.Bus;
import com.softdev.softdev.entity.BusStop;
import com.softdev.softdev.entity.RoutePath;

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
        Map<String, Object> position = BusService.showBusPosition(busId);
        Double buslat = (Double) position.get("latitude");
        Double buslon = (Double) position.get("longitude");
        BusStop stop = busStopService.getBusStopById(stopId);
        Double stoplat = stop.getGeoLocation().getLatitude();
        Double stoplon = stop.getGeoLocation().getLongitude();

        try {
            String url = String.format(
                    "http://router.project-osrm.org/route/v1/driving/%f,%f;%f,%f?overview=false",
                    buslon, buslat, stoplon, stoplat);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JSONParser parser = new JSONParser(JSONParser.MODE_PERMISSIVE);
                JSONObject obj = (JSONObject) parser.parse(response.body());

                JSONArray routes = (JSONArray) obj.get("routes");
                if (routes != null && !routes.isEmpty()) {
                    JSONObject route = (JSONObject) routes.get(0);
                    double durationSeconds = ((double) route.get("duration"));
                    return Map.of(
                            "bus_id", busId,
                            "stop_id", stopId,
                            "eta_seconds", durationSeconds);
                }
            }
        } catch (IOException | InterruptedException e) {

        }

        double BUS_SPEED_KMH = 10.0;
        double BUS_SPEED_MS = BUS_SPEED_KMH * 1000 / 3600;

        Bus bus = BusService.getBusById(busId);
        List<RoutePath> routePaths = routePathService.findRoutePathByRouteId(bus.getRoute().getRouteId());
        List<Double> cumulative = routePathService.getCumulativeDistance(routePaths);

        Map<String, Object> pos = BusService.showBusPosition(busId);
        double busLat = (double) pos.get("latitude");
        double busLon = (double) pos.get("longitude");

        double minDist = Double.MAX_VALUE;
        int nearestIndex = 0;
        for (int i = 0; i < routePaths.size(); i++) {
            double lat = routePaths.get(i).getGeoLocation().getLatitude();
            double lon = routePaths.get(i).getGeoLocation().getLongitude();
            double d = geolocationService.haversine(busLat, busLon, lat, lon);
            if (d < minDist) {
                minDist = d;
                nearestIndex = i;
            }
        }
        double busCumulativeDist = cumulative.get(nearestIndex);

        List<Double> busStopDistances = busStopService.getBusStopDistances(bus.getRoute().getRouteId(), routePaths, cumulative);
        double stopCumulativeDist = busStopDistances.get(4);

        double remainingDist = stopCumulativeDist - busCumulativeDist;
        if (remainingDist < 0) {
            remainingDist = 0;
        }
        
        double timeToReachStop = remainingDist / BUS_SPEED_MS;

        return Map.of(
            "bus_id", busId,
            "stop_id", stopId,
            "eta_seconds", timeToReachStop
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
