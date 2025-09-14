package com.softdev.softdev.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.softdev.softdev.dto.APIResponseDTO;
import com.softdev.softdev.dto.feedback_bus.CreateFeedbackBusDTO;
import com.softdev.softdev.dto.feedback_bus.FeedbackBusDTO;
import com.softdev.softdev.dto.feedback_bus.UpdateFeedbackBusDTO;
import com.softdev.softdev.entity.FeedbackBus;
import com.softdev.softdev.service.FeedbackBusService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/feedback-bus")
public class FeedbackBusController {
    @Autowired
    private FeedbackBusService feedbackBusService;
    
    @GetMapping("/bus/{busId}")
    public List<FeedbackBusDTO> GetAllFeedbackBusByBusId(@PathVariable Long busId) {
        List<FeedbackBus> feedbackBuses = feedbackBusService.getAllFeedbackBus(busId);

        return feedbackBusService.toDtoList(feedbackBuses);
    }

    @PostMapping("/createFeedbackBus")
    public ResponseEntity<?> createFeedbackBus(
        @AuthenticationPrincipal OAuth2User principal,
        @Valid @ModelAttribute CreateFeedbackBusDTO createFeedbackBusDTO
    )
    {
        FeedbackBus feedbackBus = feedbackBusService.createFeedbackBus(
            createFeedbackBusDTO.getRating(),
            createFeedbackBusDTO.getComment(),
            principal,
            createFeedbackBusDTO.getBusId()
        );

        FeedbackBusDTO feedbackBusDTO = feedbackBusService.toDto(feedbackBus);

        APIResponseDTO<FeedbackBusDTO> response = new APIResponseDTO<>();
        response.setData(feedbackBusDTO);
        response.setMessage("FeedbackBus created successfully");
        return ResponseEntity.ok(response);

    }

    @PutMapping("/updateFeedbackBus")
     public ResponseEntity<?> UpdateFeedbackBus(
        @AuthenticationPrincipal OAuth2User principal,
        @Valid @ModelAttribute UpdateFeedbackBusDTO UpdateFeedbackBusDTO 
     )
     {
        FeedbackBus feedbackBus = feedbackBusService.updateFeedbackBus(
            UpdateFeedbackBusDTO.getFeedbackBusId(),
            UpdateFeedbackBusDTO.getRating(),
            UpdateFeedbackBusDTO.getComment(),
            principal
        );

        FeedbackBusDTO feedbackBusDTO = feedbackBusService.toDto(feedbackBus);

        APIResponseDTO<FeedbackBusDTO> response = new APIResponseDTO<>();
        response.setData(feedbackBusDTO);
        response.setMessage("FeedbackBus updated successfully");
        return ResponseEntity.ok(response);
     }

    @GetMapping("/{feedbackBusId}")
    public String getMethodName(@RequestParam String param) {
        return new String();
    }

    @DeleteMapping("/{feedbackBusId}")
    public String deleteMethodName(@PathVariable String id) {
        return id;
    } 
    
    
    

}
