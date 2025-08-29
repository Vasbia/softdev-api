package com.softdev.softdev.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.softdev.softdev.entity.Place;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Long> {
    Optional<List<Place>> findAllByBusStopRouteRouteId(Long routeId);
    Optional<List<Place>> findAllByBusStopBusStopId(Long busStopId);
}
