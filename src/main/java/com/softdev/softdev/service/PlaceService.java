package com.softdev.softdev.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.softdev.softdev.dto.place.PlaceDTO;
import com.softdev.softdev.entity.BusStop;
import com.softdev.softdev.entity.Place;
import com.softdev.softdev.repository.PlaceRepository;

@Service
public class PlaceService {
    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private GeolocationService geolocationService;

    @Autowired 
    private BusStopService busStopService;

    public Place getPlaceById(Long placeId) {
        return placeRepository.findById(placeId).orElseThrow(() -> new RuntimeException("Place not found for placeId: " + placeId));
    }

    public List<Place> getPlaceByRouteId(Long routeId) {
        return placeRepository.findAllByBusStopRouteRouteId(routeId).orElseThrow(() -> new RuntimeException("Place not found for routeId: " + routeId));
    }

    public List<Place> getPlaceByStopId(Long stopId) {
        return placeRepository.findAllByBusStopBusStopId(stopId).orElseThrow(() -> new RuntimeException("Place not found for stopId: " + stopId));
    }

    public List<Place> findNearByPlace(Double userLatitude, Double userLongitude, Long routeId) {
        List<BusStop> busStops = busStopService.findAllByRouteRouteId(routeId);

        BusStop nearestBusStop = busStops.stream()
            .min(Comparator.comparingDouble(busStop -> geolocationService.haversine(userLatitude, userLongitude, busStop.getGeoLocation().getLatitude(), busStop.getGeoLocation().getLongitude())))
            .orElseThrow(() -> new RuntimeException("No busStops found"));
        
        List<Place> nearByPlace = getPlaceByStopId(nearestBusStop.getBusStopId());

        return nearByPlace;
    } 

    public PlaceDTO toDto(Place place) {
        PlaceDTO dto = new PlaceDTO();
        
        dto.setName(place.getName());
        dto.setImage(place.getImage());
        dto.setLatitude(place.getGeoLocation().getLatitude());
        dto.setLongitude(place.getGeoLocation().getLongitude());
        
        return dto;
    }

    public List<PlaceDTO> toDtos(List<Place> places) {
        return places.stream().map(this::toDto).toList();
    }
}