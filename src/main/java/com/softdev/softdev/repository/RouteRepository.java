package com.softdev.softdev.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.softdev.softdev.entity.Route;
import java.util.Optional;
import java.util.List;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {
    Optional<List<Route>> findByNameContainingIgnoreCase(String routeName);
}
