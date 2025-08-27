package com.softdev.softdev.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.softdev.softdev.entity.FeedbackBus;

@Repository
public interface FeedbackBusRepository extends JpaRepository<FeedbackBus, Long> {
}
