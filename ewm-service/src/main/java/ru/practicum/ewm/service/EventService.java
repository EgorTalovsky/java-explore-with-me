package ru.practicum.ewm.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.model.dto.event.EventFullDto;
import ru.practicum.ewm.model.dto.event.EventShortDto;
import ru.practicum.ewm.model.dto.event.NewEventDto;
import ru.practicum.ewm.model.entity.event.Event;

import java.util.List;

public interface EventService {

    List<EventFullDto> searchEventsForAdminApi(List<Long> users,
                                               List<String> states,
                                               List<Long> categories,
                                               String rangeStart,
                                               String rangeEnd,
                                               Pageable page);

    EventFullDto updateEventForAdmin(long eventId, NewEventDto event);

    List<EventFullDto> getAllEventsByUserId(long userId, Pageable page);

    EventFullDto addEvent(NewEventDto eventFullDto, long userId);

    EventFullDto getEventByIdForPrivateApi(long userId, long eventId);

    EventFullDto updateEventForPrivate(long userId, long eventId, NewEventDto event);

    List<EventShortDto> searchEventsForPublicApi(String text,
                                                 List<Long> categories,
                                                 Boolean paid,
                                                 String rangeStart,
                                                 String rangeEnd,
                                                 Boolean onlyAvailable,
                                                 String sort,
                                                 Pageable page,
                                                 String ip);

    EventFullDto getEventByIdForPublicApi(long eventId, String ip);

    Event getEventById(long eventId);

    List<EventShortDto> getEventsByIds(List<Long> ids);


}
