package ru.practicum.stat.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.stat.server.exception.IncorrectDateException;
import ru.practicum.stat.server.model.EndpointHit;
import ru.practicum.stat.server.repository.StatRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatServiceImpl implements StatService {

    private final StatRepository statRepository;

    public EndpointHitDto addHit(EndpointHitDto endpointHitDto) {
        statRepository.save(EndpointHit.builder()
                .app(endpointHitDto.getApp())
                .ip(endpointHitDto.getIp())
                .timestamp(LocalDateTime.parse(endpointHitDto.getTimestamp(),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .uri(endpointHitDto.getUri())
                .build());
        return endpointHitDto;
    }

    public List<ViewStatsDto> getStatistics(String start, String end, List<String> uris, Boolean unique) {
        List<ViewStatsDto> statistics = new ArrayList<>();
        LocalDateTime startTime = LocalDateTime.parse(start, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime endTime = LocalDateTime.parse(end, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        if (startTime.isAfter(endTime)) {
            throw new IncorrectDateException("Дата начала должна быть раньше даты окончания");
        }
        if (unique == null)
            unique = false;
        if (uris == null)
            uris = new ArrayList<>(statRepository.findAllHitsByDates(startTime, endTime));
        for (String uri : uris) {
            long endpointHit;
            if (!unique) {
                endpointHit = statRepository.findHitByUriNotUnique(startTime, endTime, uri);
            } else {
                endpointHit = statRepository.findHitByUriAndUniqueIp(startTime, endTime, uri);
            }
            statistics.add(new ViewStatsDto("ewm-service", uri, endpointHit));
        }
        return statistics.stream()
                .sorted(Comparator.comparingLong(x -> -x.getHits()))
                .collect(Collectors.toList());
    }
}


    /*

    public List<ViewStatsDto> getStats(String start, String end, List<String> uris, Boolean unique) {
        List<ViewStatsDto> stats = new ArrayList<>();
        if (uris == null)
            uris = new ArrayList<>(hitRepository.findAllHipsBetweenDates(toDateFromString(start), toDateFromString(end)));
        if (unique == null)
            unique = false;
        if (toDateFromString(start).isAfter(toDateFromString(end))) {
            throw new IncorrectDataException("Start date must be before end date");
        }
        for (String uri : uris) {
            long hits;
            if (unique) {
                hits = hitRepository.findHitByUriAndUniqueIp(toDateFromString(start), toDateFromString(end), uri);
            } else {
                hits = hitRepository.findHitByUriNotUnique(toDateFromString(start), toDateFromString(end), uri);
            }
            stats.add(EndpointHitMapper.toStatsDto("ewm-main-service", uri, hits));
        }
        return stats.stream()
                .sorted(Comparator.comparingLong(x -> -x.getHits()))
                .collect(Collectors.toList());
    }
}
*/