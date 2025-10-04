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
import com.softdev.softdev.dto.feedback_place.CreateFeedbackPlaceDTO;
import com.softdev.softdev.dto.feedback_place.FeedbackPlaceDTO;
import com.softdev.softdev.dto.feedback_place.UpdateFeedbackPlaceDTO;
import com.softdev.softdev.entity.FeedbackPlace;
import com.softdev.softdev.service.FeedbackPlaceService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/feedback-place")
public class FeedbackPlaceController {
    
    @Autowired
    private FeedbackPlaceService feedbackPlaceService;

    @PostMapping("/createFeedbackPlace")
    public ResponseEntity<?> createFeedbackPlace(
     
        @Valid @ModelAttribute CreateFeedbackPlaceDTO createFeedbackPlaceDTO
    )
    {
        FeedbackPlace feedbackPlace = feedbackPlaceService.createFeedback(
            createFeedbackPlaceDTO.getPlaceId(),
            createFeedbackPlaceDTO.getRating(),
            createFeedbackPlaceDTO.getComment(),
            createFeedbackPlaceDTO.getToken()     
        );

        FeedbackPlaceDTO feedbackPlaceDTO = feedbackPlaceService.toDto(feedbackPlace);

        APIResponseDTO<FeedbackPlaceDTO> response = new APIResponseDTO<>();
        response.setData(feedbackPlaceDTO);
        response.setMessage("FeedbackPlace created successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/place/{placeId}")
    public List<FeedbackPlaceDTO> getFeedbacksByPlaceId(@PathVariable Long placeId) {
        List<FeedbackPlace> feedbackPlaces = feedbackPlaceService.getFeedbacksByPlaceId(placeId);
        return feedbackPlaceService.toDtoList(feedbackPlaces);
    }

    @GetMapping("/{feedbackPlaceId}")
    public FeedbackPlaceDTO getFeedbackById(@PathVariable Long feedbackPlaceId) {
        FeedbackPlace feedbackPlace = feedbackPlaceService.getFeedbaakPlaceById(feedbackPlaceId);
        return feedbackPlaceService.toDto(feedbackPlace);
    }

    @PutMapping("/updateFeedbackPlace")
    public ResponseEntity<?> updateFeedbackPlace(
        @Valid @ModelAttribute UpdateFeedbackPlaceDTO updateFeedbackPlaceDTO
    )
    {
        FeedbackPlace feedbackPlace = feedbackPlaceService.updateFeedback(
            updateFeedbackPlaceDTO.getFeedbackPlaceId(),
            updateFeedbackPlaceDTO.getRating(),
            updateFeedbackPlaceDTO.getComment(),
            updateFeedbackPlaceDTO.getToken()

        );

        FeedbackPlaceDTO feedbackPlaceDTO = feedbackPlaceService.toDto(feedbackPlace);

        APIResponseDTO<FeedbackPlaceDTO> response = new APIResponseDTO<>();
        response.setData(feedbackPlaceDTO);
        response.setMessage("FeedbackPlace updated successfully");
        return ResponseEntity.ok(response);


    }

    @DeleteMapping("/{feedbackId}")
    public FeedbackPlaceDTO deleteFeedback(
        Long feedbackId,
        String token
    )
    {
        return feedbackPlaceService.toDto(feedbackPlaceService.deleteFeedback(feedbackId, token));
    }
}
