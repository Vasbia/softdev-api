package com.softdev.softdev.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.softdev.softdev.entity.Place;
import com.softdev.softdev.repository.PlaceRepository;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    @Autowired
    private PlaceRepository placeRepository;

    @GetMapping("/substring")
    public Map<String, Object> searchSubstring(@RequestParam String keyword) {
        List<Place> places = placeRepository.findByNameContainingIgnoreCase(keyword).orElseThrow(() -> new RuntimeException("No places found"));

        List<Map<String, Object>> placeResults = places.stream().map(place -> Map.<String, Object>of(
            "id", place.getPlaceId(),
            "name", place.getName(),
            "latitude", place.getGeoLocation().getLatitude(),
            "longitude", place.getGeoLocation().getLongitude()
        )).toList();

        return Map.of(
            "matches", placeResults
        );
    }
}
