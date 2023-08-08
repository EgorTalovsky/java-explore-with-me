package ru.practicum.ewm.controller.private_api;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.model.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.model.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.ewm.model.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.service.RequestService;

import javax.validation.constraints.Min;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class PrivateRequestController {
    private final RequestService requestService;

    @PostMapping("/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createRequest(@PathVariable @Min(0) long userId,
                                                 @RequestParam @Min(0) long eventId) {
        return requestService.addRequest(userId, eventId);
    }

    @GetMapping("/{userId}/requests")
    public List<ParticipationRequestDto> getUserRequests(@PathVariable @Min(0) long userId) {
        return requestService.getAllUserRequests(userId);
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public ParticipationRequestDto cancelUserRequest(@PathVariable @Min(0) long userId,
                                                     @PathVariable @Min(0) long requestId) {
        return requestService.cancelRequest(userId, requestId);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> getUserRequestsForEvent(@PathVariable @Min(0) long userId,
                                                                 @PathVariable @Min(0) long eventId) {
        return requestService.getUserRequestsForEvent(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    public EventRequestStatusUpdateResult changeUserEventRequestStatus(@PathVariable @Min(0) long userId,
                                                                       @PathVariable @Min(0) long eventId,
                                                                       @RequestBody EventRequestStatusUpdateRequest request) {
        return requestService.changeRequestStatus(userId, eventId, request);
    }


}
