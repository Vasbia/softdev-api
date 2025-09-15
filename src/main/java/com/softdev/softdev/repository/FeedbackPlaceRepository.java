package com.softdev.softdev.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.softdev.softdev.entity.FeedbackPlace;

@Repository
public interface FeedbackPlaceRepository extends JpaRepository<FeedbackPlace, Long> {
    Optional<FeedbackPlace> findById(Long feedbackPlaceId);
    Optional<List<FeedbackPlace>> findByPlace_PlaceId(Long placeId);
    boolean existsByUser_UserIdAndPlace_PlaceId(Long userId, Long placeId);
}
