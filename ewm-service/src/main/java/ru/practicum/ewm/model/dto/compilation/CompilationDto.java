package ru.practicum.ewm.model.dto.compilation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.model.dto.event.EventShortDto;

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
