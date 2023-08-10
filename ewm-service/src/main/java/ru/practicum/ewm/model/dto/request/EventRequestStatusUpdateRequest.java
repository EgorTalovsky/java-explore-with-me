package ru.practicum.ewm.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.model.entity.request.RequestState;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class EventRequestStatusUpdateRequest {
    private List<Long> requestIds;
    private RequestState status;
}
