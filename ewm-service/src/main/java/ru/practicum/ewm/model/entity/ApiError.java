package ru.practicum.ewm.model.entity;

import lombok.*;
import org.springframework.http.HttpStatus;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ApiError {
    private HttpStatus status;
    private String reason;
    private String message;
    private List<String> errors;
}
