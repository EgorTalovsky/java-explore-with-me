package ru.practicum.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.StatRepository;
import ru.practicum.ViewStatsDto;
import ru.practicum.model.EndpointHit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class StatService {
    @Autowired
    private final StatRepository statRepository;

    public EndpointHit addHit(EndpointHit endpointHit) {
        return statRepository.save(endpointHit);
    }

    public List<ViewStatsDto> getStatistics(String start, String end, List<String> uris, Boolean unique) {
        LocalDateTime startDate = LocalDateTime.parse(start, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime endDate = LocalDateTime.parse(end, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        List<ViewStatsDto> statistics = new ArrayList<>();

        if (uris == null) {
            uris = new ArrayList<>(statRepository.findAllHitsByDates(startDate, endDate));
        }

        for (String uri : uris) {
            long hits;
            if (unique) {
                hits = statRepository.findHitByUriAndUniqueIp(startDate, startDate, uri);
            } else {
                hits = statRepository.findHitByUriNotUnique(startDate, startDate, uri);
            }
            ViewStatsDto viewStatsDto = new ViewStatsDto("ewm-service", uri, hits);
            statistics.add(viewStatsDto);
        }
        return statistics.stream()
                .sorted(Comparator.comparingLong(x -> -x.getHits()))
                .collect(Collectors.toList());
    }
}
