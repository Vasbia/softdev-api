package com.softdev.softdev.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.softdev.softdev.entity.BusSchedule;

@Repository
public interface BusScheduleRepository extends JpaRepository<BusSchedule, Long> {
}
