package com.softdev.softdev.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.softdev.softdev.dto.place.PlaceDTO;
import com.softdev.softdev.entity.Place;
import com.softdev.softdev.repository.PlaceRepository;

@Service
public class PlaceService {
    @Autowired
    private PlaceRepository placeRepository;

    public List<Place> getPlaceByRouteId(Long routeId) {
        return placeRepository.findAllByBusStopRouteRouteId(routeId).orElseThrow(() -> new RuntimeException("Place not found for routeId: " + routeId));
    }

    public List<Place> getPlaceByStopId(Long stopId) {
        return placeRepository.findAllByBusStopBusStopId(stopId).orElseThrow(() -> new RuntimeException("Place not found for stopId: " + stopId));
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