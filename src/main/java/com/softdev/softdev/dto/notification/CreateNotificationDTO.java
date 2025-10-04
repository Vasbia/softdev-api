package com.softdev.softdev.dto.notification;

import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class CreateNotificationDTO {


    @NotNull(message="bus_id is required")
    private Long bus_id ;

    @NotNull(message="bus_stop_id is required")
    private Long bus_stop_id;   

    @NotNull(message="time to notify is required")
    private Long timeToNotify;

    @NotNull(message = "token is required")
    private String token;


}
