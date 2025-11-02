package com.softdev.softdev.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.softdev.softdev.entity.RoutePath;

@Repository
public interface RoutePathRepository extends JpaRepository<RoutePath, Long> {
    Optional<RoutePath> findByRoutePathId(Long routeId);
    Optional<List<RoutePath>> findAllByRouteRouteId(Long routeId);
}
