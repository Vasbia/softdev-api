package com.softdev.softdev.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.softdev.softdev.entity.BusStop;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

@Service
public class BusStopETAService {

    @Autowired
    private BusStopService busStopService;

    public Map<String, Object> ETAToStop(Double buslat, Double buslon, Long stopId) throws ParseException {
        BusStop stop = busStopService.getBusStopById(stopId);
        Double stoplat = stop.getGeoLocation().getLatitude();
        Double stoplon = stop.getGeoLocation().getLongitude();

        try {
        String url = String.format(
            "http://router.project-osrm.org/route/v1/driving/%f,%f;%f,%f?overview=false",
            buslon, buslat, stoplon, stoplat
        );

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
                double durationSeconds = ((Number) route.get("duration")).doubleValue();
                return Map.of(
                    "etaMinutes", durationSeconds
                );
            }
        }
        } catch (IOException | InterruptedException e) {
            
        }

        return Map.of(
            "etaMinutes", 99999
        );
    }
}
