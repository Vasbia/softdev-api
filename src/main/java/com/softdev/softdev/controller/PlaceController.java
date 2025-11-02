package com.softdev.softdev.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.softdev.softdev.dto.place.FindNearByPlaceDTO;
import com.softdev.softdev.dto.place.PlaceDTO;
import com.softdev.softdev.dto.place.PlaceWithoutImageDTO;
import com.softdev.softdev.entity.Place;
import com.softdev.softdev.service.PlaceService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/place")
public class PlaceController {
    @Autowired
    private PlaceService placeService;

    @GetMapping("/{placeId}")
    public PlaceDTO getPlaceById(@PathVariable Long placeId) {
        Place place = placeService.getPlaceById(placeId);
        
        return placeService.toDto(place);
    }

    @GetMapping("route/{routeId}")
    public List<PlaceWithoutImageDTO> getPlaceByRouteId(@PathVariable Long routeId) {
        List<Place> places = placeService.getPlaceByRouteId(routeId);

        return placeService.toDtosForPlaceWithoutImage(places);
    } 

    @GetMapping("stop/{stopId}")
    public List<PlaceWithoutImageDTO> getPlaceByStopId(@PathVariable Long stopId) {
        List<Place> places = placeService.getPlaceByStopId(stopId);

        return placeService.toDtosForPlaceWithoutImage(places);
    }

    @PostMapping("/findNearByPlace")
    public List<PlaceDTO> findNearByPlace(@Valid @RequestBody FindNearByPlaceDTO findNearByPlaceDTO) {
        List<Place> places = placeService.findNearByPlace(
            findNearByPlaceDTO.getUserLatitude(),
            findNearByPlaceDTO.getUserLongitude(),
            findNearByPlaceDTO.getRouteId()
        );

        return placeService.toDtos(places);
    }
}
