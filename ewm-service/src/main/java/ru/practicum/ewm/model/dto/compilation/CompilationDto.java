package ru.practicum.ewm.model.dto.compilation;

import ru.practicum.ewm.model.dto.event.EventShortDto;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class CompilationDto {
    private List<EventShortDto> events;
    private Long id;
    private boolean pinned;
    private String title;
}
