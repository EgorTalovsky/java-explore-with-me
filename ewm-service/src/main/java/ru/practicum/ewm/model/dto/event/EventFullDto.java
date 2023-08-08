package ru.practicum.ewm.model.dto.event;

import ru.practicum.ewm.model.dto.UserDto;
import ru.practicum.ewm.model.dto.CategoryDto;
import ru.practicum.ewm.model.entity.event.EventState;
import ru.practicum.ewm.model.entity.Location;
import lombok.*;


@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class EventFullDto {
    private long id;
    private String annotation;
    private CategoryDto category;
    private int confirmedRequests;
    private String createdOn;
    private String description;
    private String eventDate;
    private UserDto initiator;
    private Location location;
    private Boolean paid;
    private int participantLimit;
    private String publishedOn;
    private Boolean requestModeration;
    private EventState state;
    private String title;
    private int views;
}
