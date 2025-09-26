package com.softdev.softdev.dto.notification;

import java.time.LocalTime;

import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class CreateNotificationDTO {

    @NotNull(message="title is required")
    private String title ;

    @NotNull(message="message is required")
    private String message ;

    @NotNull(message="bus_id is required")
    private long bus_id ;

    @NotNull(message="bus_stop_id is required")
    private long bus_stop_id;   

}
