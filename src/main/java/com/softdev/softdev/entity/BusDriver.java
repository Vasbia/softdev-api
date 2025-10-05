package com.softdev.softdev.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "bus_driver")
public class BusDriver {

    @Id
    @Column(name ="user_id")
    private Long userId;

    @Column(name = "bus_id")
    private Long busId;
    
}
