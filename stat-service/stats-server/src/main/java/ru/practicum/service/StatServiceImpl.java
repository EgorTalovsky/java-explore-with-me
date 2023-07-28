package ru.practicum.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.model.EndpointHit;
import ru.practicum.repository.StatRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class StatServiceImpl implements StatService {
    private final StatRepository statRepository;

    public EndpointHit addHit(EndpointHitDto endpointHitDto) {
        EndpointHit endpointHit = new EndpointHit(
                endpointHitDto.getId(),
                endpointHitDto.getApp(),
                endpointHitDto.getUri(),
                endpointHitDto.getIp(),
                endpointHitDto.getTimestamp());
        return statRepository.save(endpointHit);
    }

    public List<ViewStatsDto> getStatistics(String start, String end, List<String> uris, Boolean unique) {
        List<ViewStatsDto> statistics = new ArrayList<>();
        LocalDateTime startTime = LocalDateTime.parse(start, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime endTime = LocalDateTime.parse(end, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
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
