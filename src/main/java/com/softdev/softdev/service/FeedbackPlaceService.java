package com.softdev.softdev.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.softdev.softdev.dto.feedback_place.FeedbackPlaceDTO;
import com.softdev.softdev.entity.FeedbackPlace;
import com.softdev.softdev.entity.Place;
import com.softdev.softdev.entity.User;
import com.softdev.softdev.exception.ResourceNotFoundException;
import com.softdev.softdev.exception.user.UserForBiddenException;
import com.softdev.softdev.exception.user.UserNotAuthenticatedException;
import com.softdev.softdev.repository.FeedbackPlaceRepository;
import com.softdev.softdev.repository.PlaceRepository;

@Service
public class FeedbackPlaceService {

    @Autowired
    private FeedbackPlaceRepository feedbackPlaceRepository;

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private UserService userService;

    public FeedbackPlace createFeedback(Long placeId, Integer rating, String comment, OAuth2User principal) {
        User user = userService.getCurrentUser(principal);
        if (user == null) {
            throw new UserNotAuthenticatedException("User is not authenticated");
        }

        Place place = placeRepository.findById(placeId).orElseThrow(() -> new ResourceNotFoundException("Place not found with id: " + placeId));

        FeedbackPlace feedback = new FeedbackPlace();
        feedback.setPlace(place);
        feedback.setUser(user);
        feedback.setRating(rating);
        feedback.setComment(comment);

        return feedbackPlaceRepository.save(feedback);
    }

    public List<FeedbackPlace> getFeedbacksByPlaceId(Long placeId) {
        return feedbackPlaceRepository.findAllByPlacePlaceId(placeId).orElseThrow(() -> new ResourceNotFoundException("No feedback found for placeId: " + placeId));
    }

    public FeedbackPlace getFeedbaakPlaceById(Long feedbackPlaceId) {
        return feedbackPlaceRepository.findById(feedbackPlaceId).orElseThrow(() -> new ResourceNotFoundException("FeedbackPlace not found with id: " + feedbackPlaceId));

    }

    public FeedbackPlace deleteFeedback(Long feedbackId, OAuth2User principal) {
        FeedbackPlace feedback = feedbackPlaceRepository.findById(feedbackId).orElseThrow(() -> new ResourceNotFoundException("Feedback not found with id: " + feedbackId));

        User user = userService.getCurrentUser(principal);
        if (user == null) {
            throw new UserNotAuthenticatedException("User is not authenticated");
        }

        if (!feedback.getUser().getUserId().equals(user.getUserId())) {
            throw new UserForBiddenException("You are not allowed to delete this feedback");
        }

        feedbackPlaceRepository.delete(feedback);
        return feedback;
    }

    public FeedbackPlace updateFeedback(Long feedbackId, Integer rating, String comment, OAuth2User principal) {
        FeedbackPlace feedback = feedbackPlaceRepository.findById(feedbackId).orElseThrow(() -> new ResourceNotFoundException("Feedback not found with id: " + feedbackId));

        User user = userService.getCurrentUser(principal);

        if (user == null) {
            throw new UserNotAuthenticatedException("User is not authenticated");
        }

        if (!feedback.getUser().getUserId().equals(user.getUserId())) {
            throw new UserForBiddenException("You are not allowed to update this feedback");
        }

        feedback.setRating(rating);
        feedback.setComment(comment);

        return feedbackPlaceRepository.save(feedback);
    }

    public FeedbackPlaceDTO toDto(FeedbackPlace feedbackPlace) {
        FeedbackPlaceDTO dto = new FeedbackPlaceDTO();
        dto.setRating(feedbackPlace.getRating());
        dto.setComment(feedbackPlace.getComment());
        dto.setPlaceId(feedbackPlace.getPlace().getPlaceId());
        return dto;
    }

    public List<FeedbackPlaceDTO> toDtoList(List<FeedbackPlace> feedbackPlaces) {
        return feedbackPlaces.stream().map(this::toDto).toList();
    }

}
