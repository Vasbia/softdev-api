package com.softdev.softdev.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.softdev.softdev.dto.APIResponseDTO;
import com.softdev.softdev.dto.feedback_application.FeedbackApplicationDTO;
import com.softdev.softdev.entity.FeedbackApplication;
import com.softdev.softdev.service.FeedbackApplicationService;

import io.swagger.v3.oas.models.responses.ApiResponse;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/api/feedback-application")
public class FeedbackApplicationController {
    @Autowired
    private FeedbackApplicationService feedbackApplicationService;

    @GetMapping("/{feedbackApplicationId}")
    public FeedbackApplicationDTO getFeedbackApplicationById(@PathVariable Long feedbackApplicationId) {
        FeedbackApplication feedbackApplication = feedbackApplicationService.getFeedbackApplicationById(feedbackApplicationId);

        return feedbackApplicationService.toDto(feedbackApplication);
    }

    @GetMapping()
    public List<FeedbackApplicationDTO> getAllFeedbackApplication(@RequestParam String param) {
        List<FeedbackApplication> feedbackApplications = feedbackApplicationService.getAllFeedbackApplication();

        return feedbackApplicationService.toDtos(feedbackApplications);
    }
    
    // @PostMapping()
    // public APIResponseDTO<> createFeedbackApplication() { // rating, comment, currentUserId
    //     return 
    // }

    // @PutMapping()
    // public APIResponseDTO<> updateFeedbackApplication() {
    //     return
    // }

    // @DeleteMapping("/{feedbackApplicationId}")
    // public APIResponseDTO<> deleteFeedbackApplication(@PathVariable Long feedbackApplicationId) {
    //     return
    // }
}
