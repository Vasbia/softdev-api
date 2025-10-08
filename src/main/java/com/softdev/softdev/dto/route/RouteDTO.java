package com.softdev.softdev.dto.route;

import java.util.List;

import com.softdev.softdev.dto.routepath.RoutePathDTO;

import lombok.Data;

@Data
public class RouteDTO {
    private Long routeId;
    private String name;
    private String description;
    private String color;
    private List<RoutePathDTO> path;
}
