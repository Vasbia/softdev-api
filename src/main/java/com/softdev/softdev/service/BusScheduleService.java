package com.softdev.softdev.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.softdev.softdev.entity.BusSchedule;
import com.softdev.softdev.exception.ResourceNotFoundException;
import com.softdev.softdev.repository.BusScheduleRepository;

@Service
public class BusScheduleService {
    @Autowired
    private BusScheduleRepository busScheduleRepository;

    public List<BusSchedule> findBusScheduleByBusId(Long busId) {
        return busScheduleRepository.findByBusBusIdOrderByRoundAsc(busId).orElseThrow(() -> new ResourceNotFoundException("No bus schedules found for busId: " + busId));
    }
}
