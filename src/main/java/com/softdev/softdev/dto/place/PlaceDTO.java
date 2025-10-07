package com.softdev.softdev.dto.place;

import lombok.Data;

@Data
public class PlaceDTO {
    private long place_id;
    private String name;
    private String image;
    private Double latitude;
    private Double longitude;
}
