package com.softdev.softdev.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.softdev.softdev.entity.Geolocation;

@Repository
public interface GeolocationRepository extends JpaRepository<Geolocation, Long> {
}
