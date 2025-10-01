package com.softdev.softdev.entity;

import java.time.LocalTime;

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
@Table(name = "bus_schedule")
public class BusSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bus_schedule_id")
    private Long busScheduleId;

    @Column(name = "arrive_time", nullable = false)
    private LocalTime arriveTime;

    @Column(name = "schedule_order", nullable = false)
    private Integer scheduleOrder;

    @Column(name = "round", nullable = false)
    private Integer round;

    @ManyToOne
    @JoinColumn(name = "bus_id")
    private Bus bus;

    @ManyToOne
    @JoinColumn(name = "bus_stop_id")
    private BusStop busStop;
}
