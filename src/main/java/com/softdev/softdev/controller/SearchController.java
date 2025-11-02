package com.softdev.softdev.controller;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.softdev.softdev.entity.BusStop;
import com.softdev.softdev.entity.Geolocation;
import com.softdev.softdev.entity.Place;
import com.softdev.softdev.entity.Route;
import com.softdev.softdev.entity.RoutePath;
import com.softdev.softdev.repository.BusStopRepository;
import com.softdev.softdev.repository.PlaceRepository;
import com.softdev.softdev.repository.RouteRepository;
import com.softdev.softdev.repository.RoutePathRepository;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private BusStopRepository busStopRepository;

    @Autowired
    private RouteRepository busRouteRepository;

    @Autowired
    private RoutePathRepository routePathRepository;

    @GetMapping("/substring")
    public Map<String, Object> searchSubstring(@RequestParam String keyword) {
        List<Place> places = placeRepository.findByNameContainingIgnoreCase(keyword).orElse(List.of());
        List<BusStop> busStops = busStopRepository.findByNameContainingIgnoreCase(keyword).orElse(List.of());
        List<Route> busRoutes = busRouteRepository.findByNameContainingIgnoreCase(keyword).orElse(List.of());

        Map<Long, Geolocation> routePathMap = busRoutes.stream()
            .map(route -> routePathRepository.findByRoutePathId(route.getRouteId())
                .map(RoutePath::getGeoLocation)
                .map(geo -> Map.entry(route.getRouteId(), geo))
                .orElse(null))
            .filter(Objects::nonNull)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        Stream<Map<String, Object>> placeStream = places.stream().map(place -> Map.<String, Object>of(
            "id", place.getPlaceId(),
            "name", place.getName(),
            "latitude", place.getGeoLocation().getLatitude(),
            "longitude", place.getGeoLocation().getLongitude(),
            "type", "landmark"
        ));

        Stream<Map<String, Object>> busStopStream = busStops.stream().map(stop -> Map.<String, Object>of(
            "id", stop.getBusStopId(),
            "name", stop.getName(),
            "latitude", stop.getGeoLocation().getLatitude(),
            "longitude", stop.getGeoLocation().getLongitude(),
            "type", "bus_stop"
        ));

        Stream<Map<String, Object>> busRouteStream = busRoutes.stream().map(route -> {
            Geolocation geo = routePathMap.get(route.getRouteId());
            return Map.<String, Object>of(
                "id", route.getRouteId(),
                "name", route.getName(),
                "latitude", geo != null ? geo.getLatitude() : null,
                "longitude", geo != null ? geo.getLongitude() : null,
                "type", "bus_route"
            );
        });

        List<Map<String, Object>> matches = Stream
            .of(placeStream, busStopStream, busRouteStream)
            .flatMap(s -> s)
            .toList();

        return Map.of("matches", matches);
    }


}
