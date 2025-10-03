package com.softdev.softdev.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.softdev.softdev.dto.feedback_application.FeedbackApplicationDTO;
import com.softdev.softdev.entity.FeedbackApplication;
import com.softdev.softdev.entity.User;
import com.softdev.softdev.exception.ResourceNotFoundException;
import com.softdev.softdev.exception.user.UserForBiddenException;
import com.softdev.softdev.exception.user.UserNotAuthenticatedException;
import com.softdev.softdev.repository.FeedbackApplicationRepository;

@Service
public class FeedbackApplicationService {
    @Autowired
    private FeedbackApplicationRepository feedbackApplicationRepository;

    @Autowired
    private UserService userService;

    public FeedbackApplication createFeedbackApplication(Integer rating, String message, OAuth2User principal) {
        User user = userService.getCurrentUser(principal);
        if (user == null) {
            throw new UserNotAuthenticatedException("User is not authenticated");
        }

        FeedbackApplication feedbackApplication = new FeedbackApplication();
        feedbackApplication.setRating(rating);
        feedbackApplication.setComment(message);
        feedbackApplication.setUser(user);

        return feedbackApplicationRepository.save(feedbackApplication);
    }

    public FeedbackApplication getFeedbackApplicationById(Long feedbackApplicationId) {
        return feedbackApplicationRepository.findById(feedbackApplicationId).orElseThrow(() -> new ResourceNotFoundException("FeedbackApplication not found with id: " + feedbackApplicationId));
    }

    public List<FeedbackApplication> getAllFeedbackApplication() {
        return feedbackApplicationRepository.findAll();
    }

    public FeedbackApplication updateFeedbackApplication(Long feedbackApplicationId, Integer rating, String message,
            OAuth2User principal) {
        User user = userService.getCurrentUser(principal);
        if (user == null) {
            throw new UserNotAuthenticatedException("User is not authenticated");
        }

        FeedbackApplication feedbackApplication = getFeedbackApplicationById(feedbackApplicationId);

        if (!feedbackApplication.getUser().getUserId().equals(user.getUserId())) {
            throw new UserForBiddenException("You are not allowed to update this feedback");
        }

        if (rating != null) {
            feedbackApplication.setRating(rating);
        }
        if (message != null) {
            feedbackApplication.setComment(message);
        }

        return feedbackApplicationRepository.save(feedbackApplication);
    }

    public FeedbackApplication deleteFeedbackApplication(Long feedbackApplcationId, OAuth2User principal) {
        User user = userService.getCurrentUser(principal);
        if (user == null) {
            throw new UserNotAuthenticatedException("User is not authenticated");
        }

        FeedbackApplication feedbackApplication = getFeedbackApplicationById(feedbackApplcationId);

        if (!feedbackApplication.getUser().getUserId().equals(user.getUserId())) {
            throw new UserForBiddenException("You are not allowed to delete this feedback");
        }

        feedbackApplicationRepository.delete(feedbackApplication);

        return feedbackApplication;
    }

    public FeedbackApplicationDTO toDto(FeedbackApplication feedbackApplication) {
        FeedbackApplicationDTO dto = new FeedbackApplicationDTO();

        dto.setFname(feedbackApplication.getUser().getFname());
        dto.setLname(feedbackApplication.getUser().getLname());
        dto.setEmail(feedbackApplication.getUser().getEmail());
        dto.setRating(feedbackApplication.getRating());
        dto.setComment(feedbackApplication.getComment());

        return dto;
    }

    public List<FeedbackApplicationDTO> toDtos(List<FeedbackApplication> feedbackApplications) {
        return feedbackApplications.stream().map(this::toDto).toList();
    }
}
