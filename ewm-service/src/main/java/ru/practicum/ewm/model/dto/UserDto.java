package ru.practicum.ewm.model.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class UserDto {
    private long id;
    @NotBlank
    private String name;
    @Email @NotBlank
    private String email;
}
