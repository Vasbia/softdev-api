package com.softdev.softdev.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.softdev.softdev.dto.feedback_bus.FeedbackBusDTO;
import com.softdev.softdev.entity.Bus;
import com.softdev.softdev.entity.FeedbackBus;
import com.softdev.softdev.entity.User;
import com.softdev.softdev.repository.FeedbackBusRepository;

@Service
public class FeedbackBusService {
    @Autowired
    private FeedbackBusRepository feedbackBusRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private BusService busService;
    
    public FeedbackBus createFeedbackBus(Integer rating, String message, OAuth2User principal, Long busId ) {
        User user = userService.getCurrentUser(principal);
        if(user == null) {
            throw new RuntimeException("User is not authenticated");
        }

        Bus bus = busService.getBusById(busId);
        if (bus == null) {
            throw new RuntimeException("Bus not found for busId: " + busId);
        }

        FeedbackBus feedbackBus = new FeedbackBus();
        feedbackBus.setRating(rating);
        feedbackBus.setComment(message);
        feedbackBus.setUser(user);
        feedbackBus.setBus(bus);

        return feedbackBusRepository.save(feedbackBus);
    }
    
    public FeedbackBus getFeedbackBusById(Long feedbackBusId) {
        FeedbackBus feedbackBus = feedbackBusRepository.findById(feedbackBusId)
                .orElseThrow(() -> new RuntimeException("FeedbackBus not found for feedbackBusId: " + feedbackBusId));
        return feedbackBus;
    }

    public List<FeedbackBus> getAllFeedbackBus(Long busId) {
        List<FeedbackBus> feedbackBuses = feedbackBusRepository.findAllByBusBusId(busId)
                .orElseThrow(() -> new RuntimeException("No FeedbackBus found for busId: " + busId));
        
        return feedbackBuses;
    }

    public FeedbackBus updateFeedbackBus(Long feedbackBusId, Integer rating, String message, OAuth2User principal) {
        User user = userService.getCurrentUser(principal);
        if(user == null) {
            throw new RuntimeException("User is not authenticated"); 
        }

        FeedbackBus feedbackBus = getFeedbackBusById(feedbackBusId);

        if(!feedbackBus.getUser().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("You are not allowed to update this feedback");
        }

        if(rating != null) {
            feedbackBus.setRating(rating);
        }
        if(message != null) {
            feedbackBus.setComment(message);
        }

        return feedbackBusRepository.save(feedbackBus);
    }

    public FeedbackBus deleteFeedbackBus(Long feedbackBusId, OAuth2User principal) {
        User user = userService.getCurrentUser(principal);
        if(user == null) {
            throw new RuntimeException("User is not authenticated"); 
        }

        FeedbackBus feedbackBus = getFeedbackBusById(feedbackBusId);

        if(!feedbackBus.getUser().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("You are not allowed to delete this feedback");
        }

        feedbackBusRepository.delete(feedbackBus);
        return feedbackBus;
    }

    public FeedbackBusDTO toDto(FeedbackBus feedbackBus) {
        FeedbackBusDTO dto = new FeedbackBusDTO();
        dto.setFname(feedbackBus.getUser().getFname());
        dto.setLname(feedbackBus.getUser().getLname());
        dto.setEmail(feedbackBus.getUser().getEmail());
        dto.setRating(feedbackBus.getRating());
        dto.setComment(feedbackBus.getComment());
        dto.setBusId(feedbackBus.getBus().getBusId());
        return dto;
    }

    public List<FeedbackBusDTO> toDtoList(List<FeedbackBus> feedbackBuses) {
        return feedbackBuses.stream().map(this::toDto).toList();
    }


}
