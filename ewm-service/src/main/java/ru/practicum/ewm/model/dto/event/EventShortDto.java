package ru.practicum.ewm.model.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.model.dto.CategoryDto;
import ru.practicum.ewm.model.dto.UserDto;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class EventShortDto {
    private Long id;
    private String annotation;
    private CategoryDto category;
    private Integer confirmedRequests;
    private String eventDate;
    private UserDto initiator;
    private Boolean paid;
    private String title;
    private Integer views;
}
