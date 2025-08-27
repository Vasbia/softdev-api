package com.softdev.softdev.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.softdev.softdev.entity.FeedbackPlace;

@Repository
public interface FeedbackPlaceRepository extends JpaRepository<FeedbackPlace, Long> {
}
