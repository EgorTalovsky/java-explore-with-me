package ru.practicum.ewm.controller.admin_api;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.model.dto.event.EventFullDto;
import ru.practicum.ewm.model.dto.event.NewEventDto;
import ru.practicum.ewm.service.EventService;

import javax.validation.constraints.Min;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/admin")
public class AdminEventController {
    private final EventService eventService;

    @GetMapping("/events")
    public List<EventFullDto> searchEvents(@RequestParam(required = false) List<Long> userIds,
                                           @RequestParam(required = false) List<String> states,
                                           @RequestParam(required = false) List<Long> categories,
                                           @RequestParam(required = false) String rangeStart,
                                           @RequestParam(required = false) String rangeEnd,
                                           @RequestParam(defaultValue = "0") int from,
                                           @RequestParam(defaultValue = "10") int size) {
        Pageable page = PageRequest.of(from / size, size);
        return eventService.searchEventsForAdminApi(userIds, states, categories, rangeStart, rangeEnd, page);
    }

    @PatchMapping("/events/{eventId}")
    public EventFullDto updateEventByAdmin(@PathVariable @Min(0) long eventId,
                                           @RequestBody NewEventDto newEventDto) {
        return eventService.updateEventForAdmin(eventId, newEventDto);
    }
}
