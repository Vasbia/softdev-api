package com.softdev.softdev.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.softdev.softdev.dto.bus_driver.BusScheduleDTO;
import com.softdev.softdev.entity.BusSchedule;
import com.softdev.softdev.repository.BusScheduleRepository;

@Service
public class BusScheduleService {
    @Autowired
    private BusScheduleRepository busScheduleRepository;

    public List<BusSchedule> findBusScheduleByBusId(Long busId) {
        return busScheduleRepository.findByBusBusIdOrderByRoundAsc(busId).orElseThrow(() -> new RuntimeException("BusSchedule not found for busId: " + busId));
    }

    public BusScheduleDTO toDto(BusSchedule busSchedule) {
        BusScheduleDTO dto = new BusScheduleDTO();
        dto.setOrder(busSchedule.getScheduleOrder());
        dto.setArriveTime(busSchedule.getArriveTime());
        dto.setRound(busSchedule.getRound());
        dto.setBusStopName(busSchedule.getBusStop().getName());
        return dto;
    }

    public List<BusScheduleDTO> toDtos(List<BusSchedule> busSchedules) {
        return busSchedules.stream().map(this::toDto).toList();
    }
}
