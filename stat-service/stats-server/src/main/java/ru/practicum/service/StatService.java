package ru.practicum.service;

import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.model.EndpointHit;

import java.util.List;

public interface StatService {

    EndpointHit addHit(EndpointHitDto endpointHitDto);

    List<ViewStatsDto> getStatistics(String start, String end, List<String> uris, Boolean unique);
}
