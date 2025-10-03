package com.softdev.softdev.dto.bus_driver;

import java.time.LocalTime;

import lombok.Data;

@Data
public class BusScheduleDTO {
    private Integer order;
    private LocalTime arriveTime;
    private Integer round;
    private String busStopName;
}
