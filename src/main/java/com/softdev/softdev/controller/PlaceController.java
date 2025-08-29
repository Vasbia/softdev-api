package com.softdev.softdev.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.softdev.softdev.dto.place.PlaceDTO;
import com.softdev.softdev.entity.Place;
import com.softdev.softdev.service.PlaceService;

@RestController
@RequestMapping("/api/place")
public class PlaceController {
    @Autowired
    private PlaceService placeService;

    @GetMapping("route/{routeId}")
    public List<PlaceDTO> getPlaceByRouteId(@PathVariable Long routeId) {
        List<Place> places = placeService.getPlaceByRouteId(routeId);
        return placeService.toDtos(places);
    } 

    @GetMapping("stop/{stopId}")
    public List<PlaceDTO> getPlaceByStopId(@PathVariable Long stopId) {
        List<Place> places = placeService.getPlaceByStopId(stopId);
        return placeService.toDtos(places);
    }
}
