package com.softdev.softdev.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.softdev.softdev.entity.BusStop;

@Repository
public interface BusStopRepository extends JpaRepository<BusStop, Long> {
    Optional<BusStop> findById(Long BusStopId);
    Optional<List<BusStop>> findAllByRouteRouteId(Long routeId);
}