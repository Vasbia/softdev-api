package com.softdev.softdev.service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.softdev.softdev.dto.busstop.BusScheduleOfBusStopDTO;
import com.softdev.softdev.dto.busstop.BusStopDTO;
import com.softdev.softdev.entity.Bus;
import com.softdev.softdev.entity.BusSchedule;
import com.softdev.softdev.entity.BusStop;
import com.softdev.softdev.entity.RoutePath;
import com.softdev.softdev.exception.ResourceNotFoundException;
import com.softdev.softdev.repository.BusRepository;
import com.softdev.softdev.repository.BusScheduleRepository;
import com.softdev.softdev.repository.BusStopRepository;

@Service
public class BusStopService {
    @Autowired
    private BusStopRepository busStopRepository;

    @Autowired
    private BusScheduleRepository busScheduleRepository;

    @Autowired
    private BusRepository busRepository;

    public BusStop getBusStopById(Long busStopId) {
        return busStopRepository.findById(busStopId).orElseThrow(() -> new ResourceNotFoundException("Bus stop not found with id: " + busStopId));
    }

    public List<BusStop> findAllByRouteRouteId(Long routeId) {
        return busStopRepository.findAllByRouteRouteId(routeId).orElseThrow(() -> new ResourceNotFoundException("No bus stops found for routeId: " + routeId));
    }

    public List<BusStop> getAllBusStops() {
        return busStopRepository.findAll();
    }   

    public List<Double> getBusStopDistances(Long routeId, List<RoutePath> routePaths, List<Double> cumulative) {
        List<BusStop> busStops = findAllByRouteRouteId(routeId);

        List<Double> busStopDistances = new ArrayList<>();

        for (BusStop stop : busStops) {
            double stopLat = stop.getGeoLocation().getLatitude();
            double stopLon = stop.getGeoLocation().getLongitude();

            for (int i = 0; i < routePaths.size(); i++) {
                double pathLat = routePaths.get(i).getGeoLocation().getLatitude();
                double pathLon = routePaths.get(i).getGeoLocation().getLongitude();

                if (Math.abs(pathLat - stopLat) == 0 && Math.abs(pathLon - stopLon) == 0) {
                    busStopDistances.add(cumulative.get(i));
                    break;
                }
            }
        }

        return busStopDistances;
    }

    public List<Double> getBusStopDistancesFromSchedule(List<BusStop> busStops, List<RoutePath> routePaths, List<Double> cumulative) {
        List<Double> busStopDistances = new ArrayList<>();
        Boolean firstSkip = false;

        for (BusStop stop : busStops) {
            if (!firstSkip) {
                firstSkip = true;
                busStopDistances.add(0.0);
                continue;
            }
            double stopLat = stop.getGeoLocation().getLatitude();
            double stopLon = stop.getGeoLocation().getLongitude();

            if (Math.abs(routePaths.get(0).getGeoLocation().getLatitude() - stopLat) <= 0.0001 && Math.abs(routePaths.get(0).getGeoLocation().getLongitude() - stopLon) <= 0.0001) {
                busStopDistances.add(cumulative.get(cumulative.size() - 1));
                break;
            }

            for (int i = 0; i < routePaths.size(); i++) {
                double pathLat = routePaths.get(i).getGeoLocation().getLatitude();
                double pathLon = routePaths.get(i).getGeoLocation().getLongitude();

                if (Math.abs(pathLat - stopLat) == 0 && Math.abs(pathLon - stopLon) == 0) {
                    busStopDistances.add(cumulative.get(i));
                    break;
                }
            }
        }

        return busStopDistances;
    }

    public BusScheduleOfBusStopDTO getBusArriveTimeSchedule(Long busStopId){
        
        BusStop busStop = getBusStopById(busStopId);
        List<BusSchedule> busSchedules = busScheduleRepository.findByBusStopAndArriveTimeAfterOrderByArriveTimeAsc(busStop, LocalTime.now())
            .orElseThrow(() -> new ResourceNotFoundException("BusSchedule at current time not found"));

        List<Map<String, Object>> listScheduleBus = new ArrayList<>();
        for (BusSchedule schedule : busSchedules) {
            Long busId = schedule.getBus().getBusId();
            Bus bus = busRepository.findById(busId).orElseThrow(() -> new ResourceNotFoundException("Bus not found with id: " + busId));
            if (bus.getActive() == false){
                continue;
            }
            Map<String, Object> map = Map.of(
                "busId", busId,
                "arriveTime", schedule.getArriveTime()
            );
            listScheduleBus.add(map);
        }        

        BusScheduleOfBusStopDTO busScheduleOfBusStopDTO = new BusScheduleOfBusStopDTO();
        busScheduleOfBusStopDTO.setBusStopname(busStop.getName());
        busScheduleOfBusStopDTO.setBusStopId(busStopId);
        busScheduleOfBusStopDTO.setBusScheduleData(listScheduleBus);

        return busScheduleOfBusStopDTO;
    
    }


    public BusStopDTO toDto(BusStop busStop) {
        BusStopDTO dto = new BusStopDTO();
        dto.setName(busStop.getName());
        dto.setLatitude(busStop.getGeoLocation().getLatitude());
        dto.setLongitude(busStop.getGeoLocation().getLongitude());
        dto.setBusStopId(busStop.getBusStopId());
        dto.setRouteId(busStop.getRoute().getRouteId());
        return dto;
    }

    public List<BusStopDTO> toDtos(List<BusStop> busStops) {
        return busStops.stream().map(this::toDto).toList();
    }
}
