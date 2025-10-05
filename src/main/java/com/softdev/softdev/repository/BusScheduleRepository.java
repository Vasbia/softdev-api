package com.softdev.softdev.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.softdev.softdev.entity.BusSchedule;

@Repository
public interface BusScheduleRepository extends JpaRepository<BusSchedule, Long> {
    Optional<List<BusSchedule>> findByBusBusIdOrderByRoundAsc(Long busId);
    Optional<BusSchedule> findArriveTimeByBus_BusIdAndBusStop_BusStopIdAndRound(
        @Param("busId") Long busId,
        @Param("busStopId") Long busStopId,
        @Param("round") Integer round
);
}
