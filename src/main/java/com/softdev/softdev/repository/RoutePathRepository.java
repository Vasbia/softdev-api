package com.softdev.softdev.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.softdev.softdev.entity.RoutePath;

@Repository
public interface RoutePathRepository extends JpaRepository<RoutePath, Long> {
}
