package com.softdev.softdev.service;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import com.softdev.softdev.entity.Bus;
import com.softdev.softdev.entity.BusSchedule;
import com.softdev.softdev.entity.BusStop;
import com.softdev.softdev.entity.RoutePath;
import com.softdev.softdev.repository.BusRepository;

@Service
public class BusService {
    @Autowired
    private BusRepository busRepository;

    @Autowired
    private RoutePathService routePathService;

    @Autowired
    private BusScheduleService busScheduleService;

    @Autowired
    private BusStopService busStopService;

    private double BUS_SPEED_KMH = 10.0;
    private double BUS_SPEED_MS = BUS_SPEED_KMH * 1000 / 3600;

    public Bus getBusById(Long busId) {
        return busRepository.findById(busId).orElseThrow(() -> new RuntimeException("Bus not found"));
    }

    public Map<String, Object> showBusPosition(Long busId) {
        Double latitude;
        Double longitude;
        boolean isStopped;

        Bus bus = getBusById(busId);

        List<RoutePath> routePaths = routePathService.findRoutePathByRouteId(bus.getRoute().getRouteId());

        List<Double> cumulative = routePathService.getCumulativeDistance(routePaths);
        System.out.println("Cumulative distances: " + cumulative);

        List<BusSchedule> busSchedules = busScheduleService.findBusScheduleByBusId(busId);
        if (busSchedules.isEmpty()) {
            throw new RuntimeException("No bus schedules found for busId: " + busId);
        }

        LocalTime start = null;
        for (BusSchedule schedule : busSchedules) {
            if (schedule.getScheduleOrder() == 1) {
                for (BusSchedule sch : busSchedules) {
                    if (sch.getRound() == schedule.getRound() && sch.getScheduleOrder() == 8) {
                        if ((schedule.getArriveTime().isBefore(LocalTime.now()) || schedule.getArriveTime().equals(LocalTime.now()))
                                && (sch.getArriveTime().isAfter(LocalTime.now()) || sch.getArriveTime().equals(LocalTime.now()))) {
                            start = schedule.getArriveTime();
                            break;
                        }
                    }
                }
            }
        }

        if (start == null) {
            throw new RuntimeException("No active bus schedule found for busId: " + busId + " at current time");
        }

        LocalTime currentTime = LocalTime.now();
        long differenceInSeconds = Duration.between(start, currentTime).toSeconds();
        System.out.println("Difference in seconds: " + differenceInSeconds);

        List<BusStop> busStops = busStopService.findAllByRouteRouteId(bus.getRoute().getRouteId());

        List<Double> busStopDistances = busStopService.getBusStopDistances(bus.getRoute().getRouteId(), routePaths,
                cumulative);
        System.out.println("Bus stop distances: " + busStopDistances);

        long dwellTime = 15;
        long totalPauseTime = 0;

        for (int i = 0; i < busStopDistances.size(); i++) {
            double stopDistance = busStopDistances.get(i);
            double timeToReachStop = stopDistance / BUS_SPEED_MS + totalPauseTime;

            if (differenceInSeconds >= timeToReachStop && differenceInSeconds < timeToReachStop + dwellTime) {
                BusStop stop = busStops.get(i);

                latitude = stop.getGeoLocation().getLatitude();
                longitude = stop.getGeoLocation().getLongitude();
                isStopped = true;
                System.out.println("Bus is stopped at stop: " + stop.getName() + " (Latitude: " + latitude
                        + ", Longitude: " + longitude + ")");

                return Map.of(
                        "latitude", latitude,
                        "longitude", longitude,
                        "isStopped", isStopped);
            }

            if (differenceInSeconds >= timeToReachStop + dwellTime) {
                totalPauseTime += dwellTime;
            }
        }

        long effectiveTime = differenceInSeconds - totalPauseTime;
        if (effectiveTime < 0)
            effectiveTime = 0;

        double traveledDistance = BUS_SPEED_MS * effectiveTime;
        System.out.println("Traveled distance: " + traveledDistance);

        for (int i = 0; i < cumulative.size() - 1; i++) {
            if (traveledDistance >= cumulative.get(i) && traveledDistance <= cumulative.get(i + 1)) {
                double ratio = (traveledDistance - cumulative.get(i)) / (cumulative.get(i + 1) - cumulative.get(i));
                latitude = routePaths.get(i).getGeoLocation().getLatitude()
                        + (routePaths.get(i + 1).getGeoLocation().getLatitude()
                                - routePaths.get(i).getGeoLocation().getLatitude()) * ratio;
                longitude = routePaths.get(i).getGeoLocation().getLongitude()
                        + (routePaths.get(i + 1).getGeoLocation().getLongitude()
                                - routePaths.get(i).getGeoLocation().getLongitude()) * ratio;
                isStopped = false;
                System.out.println("Bus position: Latitude = " + latitude + ", Longitude = " + longitude);

                return Map.of(
                        "latitude", latitude,
                        "longitude", longitude,
                        "isStopped", isStopped);
            }
        }
        return Map.of();
    }
}
