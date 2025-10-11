package com.softdev.softdev.dto.notification;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReadNotificationDTO {
    
    @NotNull(message="token is required")
    private String token;

    @NotNull(message="notification_id is required")
    private Long notification_id;
}
