package com.softdev.softdev.dto.feedback_bus;

import lombok.Data;

@Data
public class FeedbackBusDTO {
    private String fname;
    private String lname;
    private String email;
    private Integer rating;
    private String comment;
    private Long busId;
}
