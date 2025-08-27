package com.softdev.softdev.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.softdev.softdev.entity.Bus;

@Repository
public interface BusRepository extends JpaRepository<Bus, Long> {
}

