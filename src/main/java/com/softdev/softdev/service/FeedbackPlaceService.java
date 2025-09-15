package com.softdev.softdev.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.softdev.softdev.dto.feedback_place.FeedbackPlaceDTO;
import com.softdev.softdev.entity.FeedbackPlace;
import com.softdev.softdev.entity.Place;
import com.softdev.softdev.entity.User;
import com.softdev.softdev.repository.FeedbackPlaceRepository;
import com.softdev.softdev.repository.PlaceRepository;
import com.softdev.softdev.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class FeedbackPlaceService {

    @Autowired
    private FeedbackPlaceRepository feedbackPlaceRepository;
    
    @Autowired
    private PlaceRepository placeRepository;
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    public FeedbackPlace createFeedback(Long placeId, Integer rating, String comment ,  OAuth2User principal) {
        User user = userService.getCurrentUser(principal);
        if (user == null){
            throw new RuntimeException("User is not authenticated"); 
        }

        Place place = placeRepository.findById(placeId)
            .orElseThrow(() -> new EntityNotFoundException("Place not found"));

        FeedbackPlace feedback = new FeedbackPlace();
        feedback.setPlace(place);
        feedback.setUser(user);
        feedback.setRating(rating);
        feedback.setComment(comment);

        return feedbackPlaceRepository.save(feedback);
    }

    public List<FeedbackPlace> getFeedbacksByPlaceId(Long placeId) {
        return feedbackPlaceRepository.findAllByPlacePlaceId(placeId)
            .orElseThrow(() -> new EntityNotFoundException("No feedback found for placeId: " + placeId));
    }


    public FeedbackPlace deleteFeedback(Long feedbackId, OidcUser oidcUser) {
        FeedbackPlace feedback = feedbackPlaceRepository.findById(feedbackId)
            .orElseThrow(() -> new EntityNotFoundException("Feedback not found"));
            
        User user = userRepository.findByEmail(oidcUser.getEmail());
        if (user == null) {
            throw new EntityNotFoundException("User not found");
        }

        if (!feedback.getUser().getUserId().equals(user.getUserId())) {
            throw new IllegalStateException("Not authorized to delete this feedback");
        }

        feedbackPlaceRepository.delete(feedback);
        return feedback;
    }


    public FeedbackPlace updateFeedback(Long feedbackId, FeedbackPlaceDTO feedbackDTO, OidcUser oidcUser) {
        FeedbackPlace feedback = feedbackPlaceRepository.findById(feedbackId)
            .orElseThrow(() -> new EntityNotFoundException("Feedback not found"));

        User user = userRepository.findByEmail(oidcUser.getEmail());
        if (user == null) {
            throw new EntityNotFoundException("User not found");
        }

        if (!feedback.getUser().getUserId().equals(user.getUserId())) {
            throw new IllegalStateException("Not authorized to update this feedback");
        }

        feedback.setRating(feedbackDTO.getRating());
        feedback.setComment(feedbackDTO.getComment());

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
