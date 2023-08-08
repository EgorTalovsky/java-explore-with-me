package ru.practicum.ewm.model.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.model.entity.Location;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class NewEventDto {
    private String annotation;
    private Long category;
    private String description;
    private String eventDate;
    private Location location;
    private Integer confirmedRequests;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private String stateAction;
    private String title;
}
