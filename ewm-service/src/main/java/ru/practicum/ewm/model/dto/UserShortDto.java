package ru.practicum.ewm.model.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class UserShortDto {
    private long id;
    private String name;
}
