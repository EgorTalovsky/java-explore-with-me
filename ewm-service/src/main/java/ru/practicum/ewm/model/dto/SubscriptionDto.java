package ru.practicum.ewm.model.dto;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class SubscriptionDto {
    private Long id;
    @NotNull
    private Long subscriberId;
    @NotNull
    private Long initiatorId;
    @NotNull
    private Boolean isSubscribe;
    private LocalDateTime timestamp;
}