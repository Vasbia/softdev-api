package com.softdev.softdev.dto.busstop;

import lombok.Data;

@Data
public class BusStopETADTO {
    private Long bus_id;
    private Long stop_id;
    private Long eta_seconds;
}
