package ru.practicum.ewm.controller.public_api;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.model.dto.event.EventFullDto;
import ru.practicum.ewm.model.dto.event.EventShortDto;
import ru.practicum.ewm.service.EventService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/events")
public class PublicEventController {
    private final EventService eventService;
    private final HttpServletRequest request;

    @GetMapping()
    public List<EventShortDto> getEventsBySearch(@RequestParam(required = false) String text,
                                                 @RequestParam(required = false) List<Long> categories,
                                                 @RequestParam(required = false) Boolean paid,
                                                 @RequestParam(required = false) String rangeStart,
                                                 @RequestParam(required = false) String rangeEnd,
                                                 @RequestParam(required = false) Boolean onlyAvailable,
                                                 @RequestParam(required = false) String sort,
                                                 @RequestParam(defaultValue = "0") int from,
                                                 @RequestParam(defaultValue = "10") int size) {
        String ip = request.getRemoteAddr();
        Pageable page = PageRequest.of(from / size, size);
        return eventService.searchEventsForPublicApi(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, page, ip);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventById(@PathVariable long eventId) {
        String ip = request.getRemoteAddr();
        return eventService.getEventByIdForPublicApi(eventId, ip);
    }
}
