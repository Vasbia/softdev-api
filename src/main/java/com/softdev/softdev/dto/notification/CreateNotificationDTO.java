package com.softdev.softdev.dto.notification;

import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class CreateNotificationDTO {


    @NotNull(message="bus_id is required")
    private Long bus_id ;

    @NotNull(message="bus_stop_id is required")
    private Long bus_stop_id;   

    @NotNull(message="bus schedule time is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    @Schema(type = "string", example = "09:00:23", pattern = "^\\d{2}:\\d{2}:\\d{2}$")
    private LocalTime schedule_time;

    @NotNull(message="time to notify is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    @Schema(type = "string", example = "09:00:23", pattern = "^\\d{2}:\\d{2}:\\d{2}$")
    private LocalTime time_to_notify; 

    @NotNull(message = "token is required")
    private String token;


}
