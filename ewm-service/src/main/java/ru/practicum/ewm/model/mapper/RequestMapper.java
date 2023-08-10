package ru.practicum.ewm.model.mapper;

import ru.practicum.ewm.model.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.model.entity.request.Request;

import java.time.format.DateTimeFormatter;

public class RequestMapper {

    public static ParticipationRequestDto toRequestDto(Request request) {
        return ParticipationRequestDto.builder()
                .created(request.getCreated().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .requester(request.getRequester().getId())
                .event(request.getEvent().getId())
                .id(request.getId())
                .status(request.getState())
                .build();
    }
}
