package com.softdev.softdev.dto.place;

import lombok.Data;

@Data
public class PlaceWithoutImageDTO {
    private long place_id;
    private String name;
    private Double latitude;
    private Double longitude;
}
