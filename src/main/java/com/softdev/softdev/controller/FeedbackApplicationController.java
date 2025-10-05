package com.softdev.softdev.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.softdev.softdev.dto.APIResponseDTO;
import com.softdev.softdev.dto.feedback_application.CreateFeedbackApplicationDTO;
import com.softdev.softdev.dto.feedback_application.FeedbackApplicationDTO;
import com.softdev.softdev.dto.feedback_application.UpdateFeedbackApplicationDTO;
import com.softdev.softdev.entity.FeedbackApplication;
import com.softdev.softdev.service.FeedbackApplicationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/feedback-application")
public class FeedbackApplicationController {
    @Autowired
    private FeedbackApplicationService feedbackApplicationService;

    @GetMapping("/{feedbackApplicationId}")
    public FeedbackApplicationDTO getFeedbackApplicationById(@PathVariable Long feedbackApplicationId) {
        FeedbackApplication feedbackApplication = feedbackApplicationService
                .getFeedbackApplicationById(feedbackApplicationId);

        return feedbackApplicationService.toDto(feedbackApplication);
    }

    @GetMapping()
    public List<FeedbackApplicationDTO> getAllFeedbackApplication() {
        List<FeedbackApplication> feedbackApplications = feedbackApplicationService.getAllFeedbackApplication();

        return feedbackApplicationService.toDtos(feedbackApplications);
    }

    @PostMapping()
    public ResponseEntity<?> createFeedbackApplication
    (
        @Valid @ModelAttribute CreateFeedbackApplicationDTO createFeedbackApplicationDTO
    ) 
    {
        FeedbackApplication feedbackaApplication = feedbackApplicationService.createFeedbackApplication(
            createFeedbackApplicationDTO.getRating(),
            createFeedbackApplicationDTO.getComment(),
            createFeedbackApplicationDTO.getToken()
        );

        FeedbackApplicationDTO feedbackApplicationDTO = feedbackApplicationService.toDto(feedbackaApplication);

        APIResponseDTO<FeedbackApplicationDTO> response = new APIResponseDTO<>();
        response.setMessage("Create feedback application successfully.");
        response.setData(feedbackApplicationDTO);

        return ResponseEntity.ok(response); 
    }

    @PutMapping()
    public ResponseEntity<?> updateFeedbackApplication(
        @Valid @ModelAttribute UpdateFeedbackApplicationDTO updateFeedbackApplicationDTO
    ) 
    {
        FeedbackApplication feedbackaApplication = feedbackApplicationService.updateFeedbackApplication(
            updateFeedbackApplicationDTO.getFeedbackApplicationId(),
            updateFeedbackApplicationDTO.getRating(),
            updateFeedbackApplicationDTO.getComment(),
            updateFeedbackApplicationDTO.getToken()
        );

        FeedbackApplicationDTO feedbackApplicationDTO = feedbackApplicationService.toDto(feedbackaApplication);

        APIResponseDTO<FeedbackApplicationDTO> response = new APIResponseDTO<>();
        response.setMessage("Updated feedback application successfully.");
        response.setData(feedbackApplicationDTO);

        return ResponseEntity.ok(response); 
    }

    @DeleteMapping("/{feedbackApplicationId}")
    public ResponseEntity<?> deleteFeedbackApplication(
        @PathVariable Long feedbackApplicationId,
        String token
    ) 
    {
        FeedbackApplication feedbackaApplication = feedbackApplicationService.deleteFeedbackApplication(
            feedbackApplicationId,
            token
        );

        FeedbackApplicationDTO feedbackApplicationDTO = feedbackApplicationService.toDto(feedbackaApplication);

        APIResponseDTO<FeedbackApplicationDTO> response = new APIResponseDTO<>();
        response.setMessage("Deleted feedback application successfully.");
        response.setData(feedbackApplicationDTO);

        return ResponseEntity.ok(response); 
    }
}
