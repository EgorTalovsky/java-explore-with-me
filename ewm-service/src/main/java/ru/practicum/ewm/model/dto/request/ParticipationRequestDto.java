package ru.practicum.ewm.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.model.entity.request.RequestState;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ParticipationRequestDto {
    private Long id;
    private String created;
    private Long event;
    private Long requester;
    private RequestState status;
}
