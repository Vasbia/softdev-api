package com.softdev.softdev.dto.notification;

import java.time.LocalTime;

import lombok.Data;

@Data
public class NotificationDTO {
    private String title ;
    private String message ;
    private long bus_id ;
    private long bus_stop_id;   
    private LocalTime time ;
}
