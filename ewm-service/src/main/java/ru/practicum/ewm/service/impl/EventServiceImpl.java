package ru.practicum.ewm.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import ru.practicum.client.StatClient;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.ewm.exception.EntityAlreadyExistException;
import ru.practicum.ewm.exception.IncorrectFieldException;
import ru.practicum.ewm.exception.NoDataException;
import ru.practicum.ewm.model.dto.event.EventFullDto;
import ru.practicum.ewm.model.dto.event.EventShortDto;
import ru.practicum.ewm.model.dto.event.NewEventDto;
import ru.practicum.ewm.model.entity.Category;
import ru.practicum.ewm.model.entity.User;
import ru.practicum.ewm.model.entity.event.Event;
import ru.practicum.ewm.model.entity.event.EventState;
import ru.practicum.ewm.model.mapper.EventMapper;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.service.CategoryService;
import ru.practicum.ewm.service.EventService;
import ru.practicum.ewm.service.UserService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.ewm.model.mapper.EventMapper.toDateFromString;

@Service
@AllArgsConstructor
public class EventServiceImpl implements EventService {
    private final UserService userService;
    private final CategoryService categoryService;
    private final EventRepository eventRepository;
    private final StatClient statClient;

    public List<EventFullDto> searchEventsForAdminApi(List<Long> users,
                                                      List<String> states,
                                                      List<Long> categories,
                                                      String rangeStart,
                                                      String rangeEnd,
                                                      Pageable page) {
        List<EventState> eventStates;
        if (states != null)
            eventStates = states.stream()
                    .map(EventMapper::mapEventState)
                    .collect(Collectors.toList());
        else
            eventStates = null;
        LocalDateTime startDate = null;
        LocalDateTime endDate = null;
        if (rangeStart != null)
            startDate = toDateFromString(rangeStart);
        if (rangeEnd != null)
            endDate = toDateFromString(rangeEnd);
        return eventRepository.searchEventsForAdmin(users, categories, eventStates,
                        startDate, endDate, page)
                .stream()
                .map(EventMapper::toEventFullDtoFromEvent)
                .collect(Collectors.toList());
    }

    public EventFullDto updateEventForAdmin(long eventId, NewEventDto newEventDto) {
        validateEventFields(newEventDto);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NoDataException("Событие не найдено"));
        LocalDateTime now = LocalDateTime.now();
        String eventDate = newEventDto.getEventDate();
        EventState state = event.getState();
        if (!state.equals(EventState.PENDING)) {
            throw new EntityAlreadyExistException("Событие не подлежит редактированию");
        }
        if (eventDate != null && !eventDate.isEmpty()) {
            if (now.until(toDateFromString(eventDate), ChronoUnit.HOURS) < 1) {
                throw new IncorrectFieldException("Разница должна быть минимум 1 час");
            }
        }
        Category category = null;
        if (newEventDto.getCategory() != null) {
            category = categoryService.getCategoryById(newEventDto.getCategory());
        }
        EventState stateForDto = null;
        if (newEventDto.getStateAction() != null) {
            if (newEventDto.getStateAction().contains("REJECT") || newEventDto.getStateAction().contains("CANCEL")) {
                stateForDto = EventState.CANCELED;
            } else if (newEventDto.getStateAction().contains("SEND")) {
                stateForDto = EventState.PENDING;
            } else {
                stateForDto = EventState.PUBLISHED;
            }
        }
        Event newEvent = Event.builder()
                .id(event.getId())
                .annotation(newEventDto.getAnnotation() != null ? newEventDto.getAnnotation() : event.getAnnotation())
                .eventDate(newEventDto.getEventDate() != null ? toDateFromString(newEventDto.getEventDate()) : event.getEventDate())
                .confirmedRequests(newEventDto.getConfirmedRequests() != null ? newEventDto.getConfirmedRequests() : event.getConfirmedRequests())
                .isPaid(newEventDto.getPaid() != null ? newEventDto.getPaid() : event.getIsPaid())
                .isRequestModeration(newEventDto.getRequestModeration() != null ? newEventDto.getRequestModeration() : event.getIsRequestModeration())
                .participantLimit(newEventDto.getParticipantLimit() != null ? newEventDto.getParticipantLimit() : event.getParticipantLimit())
                .location(newEventDto.getLocation() != null ? newEventDto.getLocation() : event.getLocation())
                .description(newEventDto.getDescription() != null ? newEventDto.getDescription() : event.getDescription())
                .category(category != null ? category : event.getCategory())
                .title(newEventDto.getTitle() != null ? newEventDto.getTitle() : event.getTitle())
                .state(stateForDto)
                .createdOn(event.getCreatedOn())
                .initiator(event.getInitiator())
                .views(event.getViews())
                .publishedOn(null)
                .build();
        eventRepository.save(newEvent);
        return EventMapper.toEventFullDtoFromEvent(newEvent);
    }

    public List<EventFullDto> getAllEventsByUserId(long userId, Pageable page) {
        return eventRepository.getAllEventsByUserId(userId, page)
                .stream()
                .map(EventMapper::toEventFullDtoFromEvent)
                .collect(Collectors.toList());
    }

    public EventFullDto addEvent(NewEventDto newEventDto, long userId) {
        checkEventFieldsOnNull(newEventDto);
        validateEventFields(newEventDto);
        LocalDateTime eventTime = EventMapper.toDateFromString(newEventDto.getEventDate());
        if (LocalDateTime.now().until(eventTime, ChronoUnit.HOURS) < 2) {
            throw new IncorrectFieldException("Дата должна быть в будущем");
        }
        if (newEventDto.getPaid() == null) {
            newEventDto.setPaid(false);
        }
        if (newEventDto.getParticipantLimit() == null) {
            newEventDto.setParticipantLimit(0);
        }
        if (newEventDto.getRequestModeration() == null) {
            newEventDto.setRequestModeration(true);
        }
        User user = userService.getUserById(userId);
        Category category = categoryService.getCategoryById(newEventDto.getCategory());
        Event event = EventMapper.toEventFromNewEventDto(newEventDto, user, category, LocalDateTime.now());
        return EventMapper.toEventFullDtoFromEvent(eventRepository.save(event));
    }

    public EventFullDto getEventByIdForPrivateApi(long userId, long eventId) {
        userService.getUserById(userId);
        return EventMapper.toEventFullDtoFromEvent(eventRepository.findEventById(userId, eventId)
                .orElseThrow(() -> new NoDataException("Событие не найдено")));
    }

    public EventFullDto updateEventForPrivate(long userId, long eventId, NewEventDto newEventDto) {
        Event event = eventRepository.findEventById(userId, eventId)
                .orElseThrow(() -> new NoDataException("Событие не найдено"));
        Category category = null;
        if (newEventDto.getCategory() != null) {
            category = categoryService.getCategoryById(newEventDto.getCategory());
        }
        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new EntityAlreadyExistException("Событие не в ожидании, обновить его нельзя");
        }
        if (newEventDto.getEventDate() != null && !newEventDto.getEventDate().isEmpty()) {
            if (LocalDateTime.now().until(toDateFromString(newEventDto.getEventDate()), ChronoUnit.HOURS) < 2) {
                throw new IncorrectFieldException("Дата должна быть в будущем");
            }
        }
        validateEventFields(newEventDto);
        Event newEvent = EventMapper.toEventUpdate(event, newEventDto, category);
        return EventMapper.toEventFullDtoFromEvent(eventRepository.save(newEvent));
    }

    public List<EventShortDto> searchEventsForPublicApi(String text,
                                                        List<Long> categories,
                                                        Boolean paid,
                                                        String rangeStart,
                                                        String rangeEnd,
                                                        Boolean onlyAvailable,
                                                        String sort,
                                                        Pageable page,
                                                        String ip) {
        saveStatistics("/events", ip);
        LocalDateTime start;
        LocalDateTime end = null;
        if (rangeStart == null)
            start = LocalDateTime.now();
        else
            start = toDateFromString(rangeStart);
        if (rangeEnd != null) {
            end = toDateFromString(rangeEnd);
            if (start.isAfter(end))
                throw new IncorrectFieldException("Окончание события должно быть в будущем");
        }
        if (onlyAvailable == null)
            onlyAvailable = false;
        List<Event> result;
        if (onlyAvailable)
            result = eventRepository.findOnlyAvailableEventsForSearch(text, categories,
                    paid, start, end, EventState.PUBLISHED, page);
        else
            result = eventRepository.findNonAvailableEventsForSearch(text, categories,
                    paid, start, end, EventState.PUBLISHED, page);
        if (sort == null)
            sort = "EVENT_DATE";
        switch (sort) {
            case "EVENT_DATE": {
                result = result.stream()
                        .sorted(Comparator.comparing(Event::getEventDate))
                        .collect(Collectors.toList());
                break;
            }
            case "VIEWS": {
                result = result.stream()
                        .sorted(Comparator.comparingLong(x -> -x.getViews()))
                        .collect(Collectors.toList());
                break;
            }
        }
        return result.stream()
                .map(EventMapper::toEventShortDtoFromEvent)
                .collect(Collectors.toList());
    }

    public EventFullDto getEventByIdForPublicApi(long eventId, String ip) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NoDataException("Событие не найдено"));
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new NoDataException("Событие еще не опубликовано");
        }
        saveStatistics("/events/" + eventId, ip);
        String body = statClient.getStatistics("2020-05-05 00:00:00", "2050-05-05 00:00:00",
                Collections.singletonList("/events/" + eventId), true).getBody().toString();
        event.setViews(Integer.parseInt(body.substring(body.lastIndexOf("=") + 1, body.length() - 2)));
        return EventMapper.toEventFullDtoFromEvent(event);
    }

    public Event getEventById(long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NoDataException("Событие не найдено"));
    }

    public List<EventShortDto> getEventsByIds(List<Long> ids) {
        return eventRepository.findEventsByIds(ids).stream()
                .map(EventMapper::toEventShortDtoFromEvent)
                .collect(Collectors.toList());
    }

    private void saveStatistics(String uri, String ip) {
        EndpointHitDto endpointHitDto = EndpointHitDto.builder()
                .app("ewm-service")
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .uri(uri)
                .ip(ip)
                .build();
        statClient.addHit(endpointHitDto);
    }

    private void validateEventFields(NewEventDto newEventDto) {
        if (newEventDto.getAnnotation() != null) {
            if (newEventDto.getAnnotation().length() > 2000 || newEventDto.getAnnotation().length() < 20) {
                throw new IncorrectFieldException("Длина аннотации должна быть от 20 до 2000 символов");
            }
        }
        if (newEventDto.getDescription() != null) {
            if (newEventDto.getDescription().length() > 7000 || newEventDto.getDescription().length() < 20) {
                throw new IncorrectFieldException("Длина описания должна быть от 20 до 7000 символов");
            }
        }
        if (newEventDto.getTitle() != null) {
            if (newEventDto.getTitle().length() > 120 || newEventDto.getTitle().length() < 3) {
                throw new IncorrectFieldException("Длина названия должна быть от 3 до 120 символов");
            }
        }
    }

    private void checkEventFieldsOnNull(NewEventDto newEventDto) {
        if (newEventDto.getAnnotation() == null
                || newEventDto.getAnnotation().isBlank()) {
            throw new IncorrectFieldException("Аннотация null");
        }
        if (newEventDto.getTitle() == null) {
            throw new IncorrectFieldException("Название null");
        }
        if (newEventDto.getEventDate() == null) {
            throw new IncorrectFieldException("Дата события null");
        }
        if (newEventDto.getDescription() == null
                || newEventDto.getDescription().isBlank()) {
            throw new IncorrectFieldException("Описание null");
        }
        if (newEventDto.getCategory() == null) {
            throw new IncorrectFieldException("Категория null");
        }
        if (newEventDto.getLocation() == null) {
            throw new IncorrectFieldException("Местоположение null");
        }
    }
}
