package ru.practicum.ewm.controller.private_api;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.model.dto.event.EventFullDto;
import ru.practicum.ewm.model.dto.event.NewEventDto;
import ru.practicum.ewm.service.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class PrivateEventController {
    private final EventService eventService;

    @GetMapping("/{userId}/events")
    public List<EventFullDto> getAllEventsByUserId(@PathVariable @Min(0) long userId,
                                                   @RequestParam(defaultValue = "0") @Min(0) int from,
                                                   @RequestParam(defaultValue = "10") @Min(0) int size) {
        Pageable page = PageRequest.of(from / size, size);
        return eventService.getAllEventsByUserId(userId, page);
    }

    @PostMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addEvent(@PathVariable @Min(0) long userId,
                                 @RequestBody NewEventDto newEventDto) {
        return eventService.addEvent(newEventDto, userId);
    }


    @GetMapping("/{userId}/events/{eventId}")
    public EventFullDto getUserEventById(@PathVariable @Min(0) long userId,
                                         @PathVariable @Min(0) long eventId) {
        return eventService.getEventByIdForPrivateApi(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public EventFullDto updateUserEvent(@PathVariable @Min(0) long userId,
                                        @PathVariable @Min(0) long eventId,
                                        @RequestBody NewEventDto newEventDto) {
        return eventService.updateEventForPrivate(userId, eventId, newEventDto);
    }
}
