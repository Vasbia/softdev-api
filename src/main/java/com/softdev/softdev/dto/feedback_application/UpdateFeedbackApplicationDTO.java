package com.softdev.softdev.dto.feedback_application;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateFeedbackApplicationDTO {
    private Long feedbackApplicationId;

    @Min(value = 0, message = "Rating must be at least 0")
    @Max(value = 5, message = "Rating cannot be more than 5")
    private Integer rating;

    private String comment;
    private Long currentUserId;

    @NotNull(message = "token is required")
    private String token;
}
