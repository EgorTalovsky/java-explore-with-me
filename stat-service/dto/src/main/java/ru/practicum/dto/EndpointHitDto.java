package ru.practicum.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class EndpointHitDto {
    private long id;
    private String app;
    private String uri;
    private String ip;
    private String timestamp;

}
