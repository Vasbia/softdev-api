package com.softdev.softdev.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.softdev.softdev.entity.FeedbackBus;

@Repository
public interface FeedbackBusRepository extends JpaRepository<FeedbackBus, Long> {
    Optional<FeedbackBus> findById(Long feedbackBusId);
    Optional<List<FeedbackBus>> findAllByBusBusId(Long busId);
}
