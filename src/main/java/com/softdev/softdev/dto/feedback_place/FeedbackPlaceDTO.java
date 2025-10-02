package com.softdev.softdev.dto.feedback_place;

import lombok.Data;

@Data
public class FeedbackPlaceDTO {
    private Integer rating;
    private String comment;
    private Long placeId;
}