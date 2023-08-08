package ru.practicum.ewm.service;

import ru.practicum.ewm.model.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.model.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.ewm.model.dto.request.EventRequestStatusUpdateRequest;

import java.util.List;

public interface RequestService {

    ParticipationRequestDto addRequest(long userId, long eventId);

    List<ParticipationRequestDto> getAllUserRequests(long userId);

    ParticipationRequestDto cancelRequest(long userId, long requestId);

    List<ParticipationRequestDto> getUserRequestsForEvent(long userId, long eventId);

    EventRequestStatusUpdateResult changeRequestStatus(long userId, long eventId, EventRequestStatusUpdateRequest request);

}
