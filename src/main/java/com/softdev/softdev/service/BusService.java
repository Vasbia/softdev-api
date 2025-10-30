package com.softdev.softdev.service;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.softdev.softdev.dto.bus.BusDTO;
import com.softdev.softdev.entity.Bus;
import com.softdev.softdev.entity.BusSchedule;
import com.softdev.softdev.entity.BusStop;
import com.softdev.softdev.entity.RoutePath;
import com.softdev.softdev.exception.ResourceNotFoundException;
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
        return busRepository.findById(busId).orElseThrow(() -> new ResourceNotFoundException("Bus not found with id: " + busId));
    }


    public Integer getCurrentRound(Long busId) {
        List<BusSchedule> busSchedules = busScheduleService.findBusScheduleByBusId(busId);
        if (busSchedules.isEmpty()) {
            throw new RuntimeException("No bus schedules found for busId: " + busId);
        }

        Map<Integer, List<BusSchedule>> schedulesByRound = busSchedules.stream()
                .collect(Collectors.groupingBy(BusSchedule::getRound));

        LocalTime now = LocalTime.now();

        for (Map.Entry<Integer, List<BusSchedule>> entry : schedulesByRound.entrySet()) {
            Integer round = entry.getKey();
            List<BusSchedule> schedules = entry.getValue();

            Optional<BusSchedule> first = schedules.stream()
                .min(Comparator.comparing(BusSchedule::getScheduleOrder));
            Optional<BusSchedule> last = schedules.stream()
                .max(Comparator.comparing(BusSchedule::getScheduleOrder));

            if (first.isPresent() && last.isPresent()) {
                LocalTime start = first.get().getArriveTime();
                LocalTime end = last.get().getArriveTime();

                if ((now.equals(start) || now.isAfter(start)) &&
                    (now.equals(end) || now.isBefore(end))) {
                    return round;
                }
            }
        }

        throw new RuntimeException("No active bus schedule found for busId: " + busId + " at current time");
    }

    public Map<String, Object> showBusPosition_old(Long busId) {
        Double latitude;
        Double longitude;
        boolean isStopped;

        Bus bus = getBusById(busId);

        List<RoutePath> routePaths = routePathService.findRoutePathByRouteId(bus.getRoute().getRouteId());

        List<Double> cumulative = routePathService.getCumulativeDistance(routePaths);

        List<BusSchedule> busSchedules = busScheduleService.findBusScheduleByBusId(busId);
        if (busSchedules.isEmpty()) {
            throw new ResourceNotFoundException("No bus schedules found for busId: " + busId);
        }

        LocalTime startTime = null;
        Integer currentRound = null;
        for (BusSchedule schedule : busSchedules) {
            if (schedule.getScheduleOrder() == 1) {
                for (BusSchedule sch : busSchedules) {
                    if (sch.getRound() == schedule.getRound() && sch.getScheduleOrder() == 8) {
                        if ((schedule.getArriveTime().isBefore(LocalTime.now()) || schedule.getArriveTime().equals(LocalTime.now())) && 
                        (sch.getArriveTime().isAfter(LocalTime.now()) || sch.getArriveTime().equals(LocalTime.now()))) {
                            startTime = schedule.getArriveTime();
                            currentRound = schedule.getRound();
                            break;
                        }
                    }
                }
            }
        }
        if (startTime == null) {
            throw new ResourceNotFoundException("No active bus schedule found for busId: " + busId);
        }

        LocalTime currentTime = LocalTime.now();
        long differenceInSeconds = Duration.between(startTime, currentTime).toSeconds();

        List<BusStop> busStops = busStopService.findAllByRouteRouteId(bus.getRoute().getRouteId());

        List<Double> busStopDistances = busStopService.getBusStopDistances(bus.getRoute().getRouteId(), routePaths, cumulative);

        long dwellTime = 15;
        long totalPauseTime = 0;

        long nextStop = 0;

        for (int i = 0; i < busStopDistances.size(); i++) {
            double stopDistance = busStopDistances.get(i);
            double timeToReachStop = stopDistance / BUS_SPEED_MS + totalPauseTime;

            if (differenceInSeconds >= timeToReachStop && differenceInSeconds < timeToReachStop + dwellTime) {
                BusStop stop = busStops.get(i);

                latitude = stop.getGeoLocation().getLatitude();
                longitude = stop.getGeoLocation().getLongitude();
                isStopped = true;

                if (i + 1 < busStops.size()) {
                    nextStop = busStops.get(i + 1).getBusStopId();
                }

                return Map.of(
                    "latitude", latitude,
                    "longitude", longitude,
                    "isStopped", isStopped,
                    "currentRound", currentRound,
                    "nextStop", nextStop
                );
            }

            if (differenceInSeconds >= timeToReachStop + dwellTime) {
                totalPauseTime += dwellTime;
            }
        }

        long effectiveTime = differenceInSeconds - totalPauseTime;
        if (effectiveTime < 0) {
            effectiveTime = 0;
        }

        double traveledDistance = BUS_SPEED_MS * effectiveTime;

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

                for (int j = 0; j < busStopDistances.size(); j++) {
                    if (traveledDistance < busStopDistances.get(j)) {
                        nextStop = busStops.get(j).getBusStopId();
                        break;
                    }
                }

                return Map.of(
                    "latitude", latitude,
                    "longitude", longitude,
                    "isStopped", isStopped,
                    "currentRound", currentRound,
                    "nextStop", nextStop
                );
            }
        }

        throw new ResourceNotFoundException("No Bus active at current time");
        // return Map.of();
    }

    public Map<String, Object> showBusPosition(Long busId) {
        Bus bus = getBusById(busId);
        List<RoutePath> routePaths = routePathService.findRoutePathByRouteId(bus.getRoute().getRouteId());
        List<Double> cumulative = routePathService.getCumulativeDistance(routePaths);

        List<BusSchedule> busSchedules = busScheduleService.findBusScheduleByBusId(busId);
        if (busSchedules.isEmpty()) {
            throw new ResourceNotFoundException("No bus schedules found for busId: " + busId);
        }

        Integer currentRound = getCurrentRound(busId);
        if (currentRound == null) {
            throw new ResourceNotFoundException("No active round found for busId: " + busId);
        }

        LocalTime now = LocalTime.now();
        List<BusSchedule> roundSchedules = busSchedules.stream()
                .filter(s -> s.getRound().equals(currentRound))
                .sorted(Comparator.comparing(BusSchedule::getScheduleOrder))
                .collect(Collectors.toList());

        List<BusStop> busStops = roundSchedules.stream()
                .map(BusSchedule::getBusStop)
                .collect(Collectors.toList());
        List<Double> busStopDistances = busStopService.getBusStopDistancesFromSchedule(busStops, routePaths, cumulative);

        long dwellTimeSeconds = 15;

        for (int i = 0; i < roundSchedules.size() - 1; i++) {
            BusSchedule curr = roundSchedules.get(i);
            BusSchedule next = roundSchedules.get(i + 1);

            LocalTime arriveTime = curr.getArriveTime();
            LocalTime departTime = arriveTime.plusSeconds(dwellTimeSeconds);
            LocalTime nextArriveTime = next.getArriveTime();

            if ((now.isAfter(arriveTime) || now.equals(arriveTime)) && now.isBefore(departTime)) {
                BusStop stop = busStops.get(i);
                return Map.of(
                    "latitude", stop.getGeoLocation().getLatitude(),
                    "longitude", stop.getGeoLocation().getLongitude(),
                    "isStopped", true,
                    "currentRound", currentRound,
                    "nextStop", busStops.get(i + 1).getBusStopId()
                );
            }

            if ((now.isAfter(departTime) || now.equals(departTime)) && now.isBefore(nextArriveTime)) {
                double segmentStartDist = busStopDistances.get(i);
                double segmentEndDist = busStopDistances.get(i + 1);
                double segmentLength = segmentEndDist - segmentStartDist;

                long travelSeconds = Duration.between(departTime, nextArriveTime).toSeconds();
                long elapsedSeconds = Duration.between(departTime, now).toSeconds();
                double progress = (double) elapsedSeconds / travelSeconds;
                progress = Math.max(0, Math.min(1, progress));

                double traveledDist = segmentStartDist + segmentLength * progress;

                for (int j = 0; j < cumulative.size() - 1; j++) {
                    double d1 = cumulative.get(j);
                    double d2 = cumulative.get(j + 1);

                    if (traveledDist >= d1 && traveledDist <= d2) {
                        double ratio = (traveledDist - d1) / (d2 - d1);

                        double lat = routePaths.get(j).getGeoLocation().getLatitude()
                                + (routePaths.get(j + 1).getGeoLocation().getLatitude()
                                - routePaths.get(j).getGeoLocation().getLatitude()) * ratio;
                        double lon = routePaths.get(j).getGeoLocation().getLongitude()
                                + (routePaths.get(j + 1).getGeoLocation().getLongitude()
                                - routePaths.get(j).getGeoLocation().getLongitude()) * ratio;

                        return Map.of(
                            "latitude", lat,
                            "longitude", lon,
                            "isStopped", false,
                            "currentRound", currentRound,
                            "nextStop", busStops.get(i + 1).getBusStopId()
                        );
                    }
                }
            }
        }

        throw new ResourceNotFoundException("No Bus active at current time");
    }

    public boolean isActive(Long busId){
        try {
            getCurrentRound(busId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<Bus> findAllByRouteId(Long routeId) {
        return busRepository.findAllByRouteRouteId(routeId);
    }

    public BusDTO toDto(Bus bus) {
        BusDTO dto = new BusDTO();
        dto.setBusId(bus.getBusId());
        dto.setType(bus.getType());
        dto.setRouteId(bus.getRoute().getRouteId());
        return dto;
    }

    public List<BusDTO> toDtos(List<Bus> buses) {
        return buses.stream().map(this::toDto).toList();
    }
}