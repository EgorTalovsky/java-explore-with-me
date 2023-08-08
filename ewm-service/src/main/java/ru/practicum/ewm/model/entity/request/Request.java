package ru.practicum.ewm.model.entity.request;

import ru.practicum.ewm.model.entity.User;
import ru.practicum.ewm.model.entity.event.Event;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Table(name = "requests")
@Entity
public class Request {
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    @JoinColumn(name = "requester_id")
    private User requester;
    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;
    @Enumerated(EnumType.STRING)
    private RequestState state;
}
