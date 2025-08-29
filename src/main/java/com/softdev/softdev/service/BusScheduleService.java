package com.softdev.softdev.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.softdev.softdev.entity.BusSchedule;
import com.softdev.softdev.repository.BusScheduleRepository;

@Service
public class BusScheduleService {
    @Autowired
    private BusScheduleRepository busScheduleRepository;

    public BusSchedule findBusScheduleByBusId(Long busId) {
        return busScheduleRepository.findByBusBusId(busId).orElseThrow(() -> new RuntimeException("BusSchedule not found for busId: " + busId));
    }
}
