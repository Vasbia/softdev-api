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

        @Valid @ModelAttribute CreateFeedbackBusDTO createFeedbackBusDTO
    )
    {
        FeedbackBus feedbackBus = feedbackBusService.createFeedbackBus(
            createFeedbackBusDTO.getRating(),
            createFeedbackBusDTO.getComment(),
            createFeedbackBusDTO.getToken(),
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
        @Valid @ModelAttribute UpdateFeedbackBusDTO UpdateFeedbackBusDTO 
     )
     {
        FeedbackBus feedbackBus = feedbackBusService.updateFeedbackBus(
            UpdateFeedbackBusDTO.getFeedbackBusId(),
            UpdateFeedbackBusDTO.getRating(),
            UpdateFeedbackBusDTO.getComment(),
            UpdateFeedbackBusDTO.getToken()
        
        );

        FeedbackBusDTO feedbackBusDTO = feedbackBusService.toDto(feedbackBus);

        APIResponseDTO<FeedbackBusDTO> response = new APIResponseDTO<>();
        response.setData(feedbackBusDTO);
        response.setMessage("FeedbackBus updated successfully");
        return ResponseEntity.ok(response);
     }

    @GetMapping("/{feedbackBusId}")
    public FeedbackBusDTO getFeedbackBusbyId(@PathVariable Long feedbackBusId) {
        FeedbackBus feedbackBus = feedbackBusService.getFeedbackBusById(feedbackBusId);

        return feedbackBusService.toDto(feedbackBus);
    }

    @DeleteMapping("/{feedbackBusId}")
    public FeedbackBusDTO deleteFeedbackBus(
        @PathVariable Long feedbackBusId,
        String token
    ) 
    {
        FeedbackBus feedbackBus = feedbackBusService.deleteFeedbackBus(feedbackBusId, token);

        return feedbackBusService.toDto(feedbackBus);
    } 
    
    
    

}
