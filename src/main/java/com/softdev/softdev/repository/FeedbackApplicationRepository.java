package com.softdev.softdev.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.softdev.softdev.entity.FeedbackApplication;

@Repository
public interface FeedbackApplicationRepository extends JpaRepository<FeedbackApplication, Long> {
    Optional<FeedbackApplication> findById(Long feedbackApplicationId);
}
