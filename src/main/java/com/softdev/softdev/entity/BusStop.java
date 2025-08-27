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
@Table(name = "bus_stop")
public class BusStop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bus_stop_id")
    private Long busStopId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "stop_order", nullable = true)
    private Integer stopOrder;

    @ManyToOne
    @JoinColumn(name = "geolocation_id")
    private GeoLocation geoLocation;

    @ManyToOne
    @JoinColumn(name = "route_id")
    private Route route;
}
