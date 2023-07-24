package ru.practicum;


import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@RestController
@AllArgsConstructor
public class StatController {
    private final StatClient client;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void save(@Valid @RequestBody EndpointHitDto endpointHitDto) {
        client.addHit(endpointHitDto);
    }

    @GetMapping("/stats")
    public ResponseEntity<Object> getStatistics(@NotEmpty @RequestParam(value = "start") String start,
                                                @NotEmpty @RequestParam(value = "end") String end,
                                                @RequestParam(required = false) List<String> uris,
                                                @RequestParam(defaultValue = "false") Boolean unique) {
        return client.getStatistics(start, end, uris, unique);
    }
}

