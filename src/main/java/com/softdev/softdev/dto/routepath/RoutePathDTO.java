package com.softdev.softdev.dto.routepath;

import lombok.Data;

@Data
public class RoutePathDTO {
    private Double latitude;
    private Double longitude;
    private Integer pathOrder;
    private Long routeId;
}
