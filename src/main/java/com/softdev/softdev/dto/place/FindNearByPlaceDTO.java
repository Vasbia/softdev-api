package com.softdev.softdev.dto.place;

import lombok.Data;

@Data
public class FindNearByPlaceDTO {
    private Double userLatitude;
    private Double userLongitude;
    private Long routeId;
}
