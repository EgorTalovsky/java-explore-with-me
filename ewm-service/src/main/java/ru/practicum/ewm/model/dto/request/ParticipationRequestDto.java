package ru.practicum.ewm.model.dto.request;

import ru.practicum.ewm.model.entity.request.RequestState;
import lombok.*;

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
