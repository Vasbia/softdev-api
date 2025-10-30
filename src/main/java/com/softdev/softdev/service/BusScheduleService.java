package com.softdev.softdev.service;

import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.softdev.softdev.dto.bus_driver.BusScheduleDTO;
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
    public LocalTime findBusScheduleTime(Long busId, Long busStopId, Integer currentRound){
        List<BusSchedule> busSchedules = busScheduleRepository.findAllArriveTimeByBus_BusIdAndBusStop_BusStopIdAndRound(busId, busStopId, currentRound)
                .orElseThrow(() -> new RuntimeException("BusSchedule not found for busId: " + busId + ", busStopId: " + busStopId + ", round: " + currentRound));

        return busSchedules.get(0).getArriveTime();
    }


}
