package com.softdev.softdev.dto.bus_driver;

import com.softdev.softdev.service.BusDriverService.DrivingStatus;

import lombok.Data;

@Data
public class BusStatusDTO {
    private double latitude;
    private double longitude;
    private boolean isStopped;
    private Enum<DrivingStatus> status;
    private Long difference_seconds;
    private Object remaining_schedule;
}