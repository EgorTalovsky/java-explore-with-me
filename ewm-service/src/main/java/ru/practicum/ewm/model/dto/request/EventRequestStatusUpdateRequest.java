package ru.practicum.ewm.model.dto.request;

import ru.practicum.ewm.model.entity.request.RequestState;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class EventRequestStatusUpdateRequest {
    private List<Long> requestIds;
    private RequestState status;
}
