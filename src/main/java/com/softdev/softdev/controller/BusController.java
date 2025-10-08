package com.softdev.softdev.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.softdev.softdev.dto.APIResponseDTO;
import com.softdev.softdev.service.BusService;

@RestController
@RequestMapping("/api/bus")
public class BusController {
    @Autowired
    private BusService busService;

    @GetMapping("/position/{busId}")
    public  ResponseEntity<?>  getBusPosition(@PathVariable Long busId) {
        try {
            Map<String, Object> DataBusPosition = busService.showBusPosition(busId);
            APIResponseDTO<Map<String, Object>> response = new APIResponseDTO<>();
            response.setData(DataBusPosition);

            response.setMessage("BUS_ACTIVE");
            return ResponseEntity.ok(response);
    
        } catch (Exception e) {

            System.out.println(e.getMessage());

            APIResponseDTO<Map<String, Object>> response = new APIResponseDTO<>();
            response.setMessage("NO_BUS_ACTIVE");

            return ResponseEntity.ok(response);
        }

    }
}