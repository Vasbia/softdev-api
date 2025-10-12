package com.softdev.softdev.dto.busstop;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class BusScheduleOfBusStopDTO {

    private String busStopname;
    
    private Long busStopId;

    private List<Map<String, Object>> BusScheduleData; 

}
