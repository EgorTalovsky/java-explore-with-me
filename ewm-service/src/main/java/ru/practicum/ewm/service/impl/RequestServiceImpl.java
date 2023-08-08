package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.exception.EntityAlreadyExistException;
import ru.practicum.ewm.exception.ForbiddenRequestException;
import ru.practicum.ewm.exception.NoDataException;
import ru.practicum.ewm.model.mapper.RequestMapper;
import ru.practicum.ewm.model.entity.User;
import ru.practicum.ewm.model.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.model.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.ewm.model.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.model.entity.event.Event;
import ru.practicum.ewm.model.entity.event.EventState;
import ru.practicum.ewm.model.entity.request.Request;
import ru.practicum.ewm.model.entity.request.RequestState;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.RequestRepository;
import ru.practicum.ewm.service.EventService;
import ru.practicum.ewm.service.RequestService;
import ru.practicum.ewm.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserService userService;
    private final EventService eventService;
    private final EventRepository eventRepository;

    public ParticipationRequestDto addRequest(long userId, long eventId) {
        if (requestRepository.findUserRequestToEvent(eventId, userId).isPresent())
            throw new EntityAlreadyExistException("Запрос уже подан");
        Event event = eventService.getEventById(eventId);
        User user = userService.getUserById(userId);
        validateRequestFields(event, userId);
        RequestState state;
        if (!event.getIsRequestModeration() || event.getParticipantLimit() == 0) {
            state = RequestState.CONFIRMED;
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
        } else {
            state = RequestState.PENDING;
        }
        Request request = Request.builder()
                .created(LocalDateTime.now())
                .state(state)
                .requester(user)
                .event(event)
                .build();
        requestRepository.save(request);
        eventRepository.save(event);
        return RequestMapper.toRequestDto(request);
    }

    public List<ParticipationRequestDto> getAllUserRequests(long userId) {
        userService.getUserById(userId);
        List<Request> requests = requestRepository.findUserRequests(userId);
        return requests.stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    public ParticipationRequestDto cancelRequest(long userId, long requestId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NoDataException("Запрос не найден"));
        if (userId != request.getRequester().getId()) {
            throw new ForbiddenRequestException("Это не Ваш запрос");
        }
        request.setState(RequestState.CANCELED);
        requestRepository.save(request);
        return RequestMapper.toRequestDto(request);
    }

    public List<ParticipationRequestDto> getUserRequestsForEvent(long userId, long eventId) {
        userService.getUserById(userId);
        eventService.getEventById(eventId);
        List<Request> requests = requestRepository.findRequestsToEvent(eventId);
        return requests.stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());
    }


    public EventRequestStatusUpdateResult changeRequestStatus(long userId, long eventId, EventRequestStatusUpdateRequest request) {
        userService.getUserById(userId);
        Event userEvent = eventService.getEventById(eventId);
        if (userId != userEvent.getInitiator().getId()) {
            throw new EntityAlreadyExistException("Вы не создатель события");
        }
        if (userEvent.getConfirmedRequests() >= userEvent.getParticipantLimit()) {
            throw new EntityAlreadyExistException("Нет свободных мест");
        }
        if (!userEvent.getState().equals(EventState.PUBLISHED)) {
            throw new EntityAlreadyExistException("Событие не опубликовано");
        }
        List<Request> requests = requestRepository.findRequestsByIds(request.getRequestIds());
        List<ParticipationRequestDto> confirmedRequest = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequest = new ArrayList<>();
        for (Request req : requests) {
            if (request.getStatus().equals(RequestState.CONFIRMED)) {
                if (userEvent.getParticipantLimit() == 0 || !userEvent.getIsRequestModeration()) {
                    req.setState(RequestState.CONFIRMED);
                    confirmedRequest.add(RequestMapper.toRequestDto(req));
                } else if (userEvent.getConfirmedRequests() < userEvent.getParticipantLimit()) {
                    req.setState(RequestState.CONFIRMED);
                    userEvent.setConfirmedRequests(userEvent.getConfirmedRequests() + 1);
                    confirmedRequest.add(RequestMapper.toRequestDto(req));
                } else {
                    req.setState(RequestState.REJECTED);
                    rejectedRequest.add(RequestMapper.toRequestDto(req));
                }
            } else {
                req.setState(RequestState.REJECTED);
                rejectedRequest.add(RequestMapper.toRequestDto(req));
            }
            requestRepository.save(req);
        }
        eventRepository.save(userEvent);
        return EventRequestStatusUpdateResult.builder()
                .confirmedRequests(confirmedRequest)
                .rejectedRequests(rejectedRequest)
                .build();
    }

    private void validateRequestFields(Event event, long userId) {
        if (event.getInitiator().getId() == userId)
            throw new EntityAlreadyExistException("Вы инициатор события");
        if (!event.getState().equals(EventState.PUBLISHED))
            throw new EntityAlreadyExistException("Событие не опубликовано");
        if ((event.getConfirmedRequests() >= event.getParticipantLimit())
                && (event.getConfirmedRequests() != 0 && event.getParticipantLimit() != 0))
            throw new EntityAlreadyExistException("Нет свободных мест");
    }
}
