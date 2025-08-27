package com.softdev.softdev.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "route_path")
public class RoutePath {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "route_path_id")
    private Long routePathId;

    @Column(name = "point_order", nullable = true)
    private Integer pointOrder;

    @ManyToOne
    @JoinColumn(name = "geolocation_id")
    private Geolocation geoLocation;

    @ManyToOne
    @JoinColumn(name = "route_id")
    private Route route;
}
