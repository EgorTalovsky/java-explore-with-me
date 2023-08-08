package ru.practicum.ewm.model.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class CategoryDto {
    private long id;
    @NotBlank
    private String name;
}
