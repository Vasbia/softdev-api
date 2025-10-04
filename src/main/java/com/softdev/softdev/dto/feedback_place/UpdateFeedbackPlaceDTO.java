package com.softdev.softdev.dto.feedback_place;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateFeedbackPlaceDTO {
    @NotNull(message="FeedbackPlaceId is required")
    private Long feedbackPlaceId;

    @NotNull(message="rating is required")
    @Min(value= 0, message="0 < rating < 5")
    @Max(value= 5, message="0 < rating < 5")
    private Integer rating;

    
    private String comment;

    @NotNull(message = "token is required")
    private String token;
}
