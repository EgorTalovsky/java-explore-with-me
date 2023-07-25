package ru.practicum.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.model.EndpointHit;
import ru.practicum.service.StatService;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@RestController
@AllArgsConstructor
public class StatController {
    @Autowired
    private final StatService statService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public EndpointHit save(@Valid @RequestBody EndpointHitDto endpointHitDto) {
        EndpointHit endpointHit = new EndpointHit(
                endpointHitDto.getId(),
                endpointHitDto.getApp(),
                endpointHitDto.getUri(),
                endpointHitDto.getIp(),
                endpointHitDto.getTimestamp());
        return statService.addHit(endpointHit);
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> getStatistics(@NotEmpty @RequestParam(value = "start") String start,
                                            @NotEmpty @RequestParam(value = "end") String end,
                                            @RequestParam(required = false) List<String> uris,
                                            @RequestParam(defaultValue = "false") Boolean unique) {
        return statService.getStatistics(start, end, uris, unique);
    }
}