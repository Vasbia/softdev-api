package com.softdev.softdev.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.softdev.softdev.entity.BusDriver;

@Repository
public interface BusDriverRepositiory extends JpaRepository<BusDriver, Long> {
    BusDriver findByUserId(Long userId);
}

