package com.softdev.softdev.dto.busstop;

import lombok.Data;

@Data
public class BusStopDTO {
    private String name;
    private Long busStopId;
    private Double latitude;
    private Double longitude;
    private Long routeId;
}
