package com.softdev.softdev.service;

import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.softdev.softdev.entity.BusSchedule;
import com.softdev.softdev.repository.BusScheduleRepository;

@Service
public class BusScheduleService {
    @Autowired
    private BusScheduleRepository busScheduleRepository;

    public List<BusSchedule> findBusScheduleByBusId(Long busId) {
        return busScheduleRepository.findByBusBusIdOrderByRoundAsc(busId).orElseThrow(() -> new RuntimeException("BusSchedule not found for busId: " + busId));
    }

    public LocalTime findBusScheduleTime(Long busId, Long busStopId, Integer currentRound){
      BusSchedule busSchedule = busScheduleRepository.findArriveTimeByBus_BusIdAndBusStop_BusStopIdAndRound(busId, busStopId, currentRound)
                .orElseThrow(() -> new RuntimeException("BusSchedule not found for busId: " + busId + ", busStopId: " + busStopId + ", round: " + currentRound));
        return busSchedule.getArriveTime();
    }


}
