package ru.practicum.ewm.model.dto.compilation;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class UpdateCompilationRequest {
    private List<Long> events;
    private Boolean pinned;
    @NotBlank
    private String title;
}
