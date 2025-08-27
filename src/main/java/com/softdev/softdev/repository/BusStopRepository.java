package com.softdev.softdev.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.softdev.softdev.entity.BusStop;

@Repository
public interface BusStopRepository extends JpaRepository<BusStop, Long> {
}