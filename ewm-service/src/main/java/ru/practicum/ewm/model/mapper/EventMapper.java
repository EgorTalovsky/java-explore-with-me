package ru.practicum.ewm.model.mapper;

import ru.practicum.ewm.exception.IncorrectFieldException;
import ru.practicum.ewm.model.entity.Category;
import ru.practicum.ewm.model.entity.User;
import ru.practicum.ewm.model.dto.CategoryDto;
import ru.practicum.ewm.model.dto.UserDto;
import ru.practicum.ewm.model.dto.event.EventFullDto;
import ru.practicum.ewm.model.dto.event.EventShortDto;
import ru.practicum.ewm.model.dto.event.NewEventDto;
import ru.practicum.ewm.model.entity.event.Event;
import ru.practicum.ewm.model.entity.event.EventState;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EventMapper {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static LocalDateTime toDateFromString(String dateString) {
        return LocalDateTime.parse(dateString, FORMATTER);
    }

    public static Event toEventFromNewEventDto(NewEventDto newEventDto, User user, Category category, LocalDateTime createdOn) {
        return Event.builder()
                .initiator(user)
                .state(EventState.PENDING)
                .annotation(newEventDto.getAnnotation())
                .eventDate(toDateFromString(newEventDto.getEventDate()))
                .confirmedRequests(newEventDto.getConfirmedRequests() != null ? newEventDto.getConfirmedRequests() : 0)
                .isPaid(newEventDto.getPaid())
                .isRequestModeration(newEventDto.getRequestModeration())
                .participantLimit(newEventDto.getParticipantLimit())
                .location(newEventDto.getLocation())
                .description(newEventDto.getDescription())
                .createdOn(createdOn)
                .category(category)
                .title(newEventDto.getTitle())
                .build();
    }

    public static EventFullDto toEventFullDtoFromEvent(Event event) {
        UserDto user = UserMapper.toUserDto(event.getInitiator());
        CategoryDto categoryDto = CategoryMapper.toCategoryDto(event.getCategory());
        return EventFullDto.builder()
                .id(event.getId())
                .eventDate(event.getEventDate().format(FORMATTER))
                .category(categoryDto)
                .initiator(user)
                .annotation(event.getAnnotation())
                .confirmedRequests(event.getConfirmedRequests())
                .createdOn(event.getCreatedOn().format(FORMATTER))
                .description(event.getDescription())
                .location(event.getLocation())
                .paid(event.getIsPaid())
                .participantLimit(event.getParticipantLimit())
                .state(event.getState())
                .publishedOn(event.getPublishedOn() != null ? event.getPublishedOn().format(FORMATTER) : null)
                .title(event.getTitle())
                .requestModeration(event.getIsRequestModeration())
                .views(event.getViews() != null ? event.getViews() : 0)
                .build();
    }

    public static Event toEventUpdate(Event event, NewEventDto newEventDto, Category newCategory) {
        EventState state = null;
        if (newEventDto.getStateAction() != null) {
            if (newEventDto.getStateAction().contains("REJECT") || newEventDto.getStateAction().contains("CANCEL"))
                state = EventState.CANCELED;
            else if (newEventDto.getStateAction().contains("SEND"))
                state = EventState.PENDING;
            else
                state = EventState.PUBLISHED;
        }
        return Event.builder()
                .id(event.getId())
                .annotation(newEventDto.getAnnotation() != null ? newEventDto.getAnnotation() : event.getAnnotation())
                .eventDate(newEventDto.getEventDate() != null ? toDateFromString(newEventDto.getEventDate()) : event.getEventDate())
                .confirmedRequests(newEventDto.getConfirmedRequests() != null ? newEventDto.getConfirmedRequests() : event.getConfirmedRequests())
                .isPaid(newEventDto.getPaid() != null ? newEventDto.getPaid() : event.getIsPaid())
                .isRequestModeration(newEventDto.getRequestModeration() != null ? newEventDto.getRequestModeration() : event.getIsRequestModeration())
                .participantLimit(newEventDto.getParticipantLimit() != null ? newEventDto.getParticipantLimit() : event.getParticipantLimit())
                .location(newEventDto.getLocation() != null ? newEventDto.getLocation() : event.getLocation())
                .description(newEventDto.getDescription() != null ? newEventDto.getDescription() : event.getDescription())
                .category(newCategory != null ? newCategory : event.getCategory())
                .title(newEventDto.getTitle() != null ? newEventDto.getTitle() : event.getTitle())
                .state(state)
                .createdOn(event.getCreatedOn())
                .initiator(event.getInitiator())
                .views(event.getViews())
                .publishedOn(null)
                .build();
    }

    public static EventShortDto toEventShortDtoFromEvent(Event event) {
        UserDto user = UserMapper.toUserDto(event.getInitiator());
        CategoryDto categoryDto = CategoryMapper.toCategoryDto(event.getCategory());
        return EventShortDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .paid(event.getIsPaid())
                .views(event.getViews())
                .confirmedRequests(event.getConfirmedRequests())
                .annotation(event.getAnnotation())
                .category(categoryDto)
                .eventDate(event.getEventDate().format(FORMATTER))
                .initiator(user)
                .build();
    }

    public static EventState mapEventState(String state) {
        try {
            return EventState.valueOf(state);
        } catch (Exception e) {
            throw new IncorrectFieldException("Некорректный статус");
        }
    }
}
