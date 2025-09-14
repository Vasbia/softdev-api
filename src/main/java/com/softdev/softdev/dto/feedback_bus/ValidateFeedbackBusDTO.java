package com.softdev.softdev.dto.feedback_bus;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ValidateFeedbackBusDTO {

    @NotNull(message = "BusId is required")
    private Long busId;

    @NotNull(message = "Rating is required")
    @Min(value = 0, message = "Rating must be at least 0")
    @Max(value = 5, message = "Rating cannot be more than 5")
    private Integer rating;
    
    private String comment;
    
}
