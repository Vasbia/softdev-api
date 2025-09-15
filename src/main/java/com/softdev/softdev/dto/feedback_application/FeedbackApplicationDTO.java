package com.softdev.softdev.dto.feedback_application;

import lombok.Data;

@Data
public class FeedbackApplicationDTO {
    private String fname;
    private String lname;
    private String email;
    private Integer rating;
    private String comment;
}
