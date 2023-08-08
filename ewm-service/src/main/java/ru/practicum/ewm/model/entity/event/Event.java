package ru.practicum.ewm.model.entity.event;

import ru.practicum.ewm.model.entity.Category;
import ru.practicum.ewm.model.entity.Location;
import ru.practicum.ewm.model.entity.User;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Table(name = "events")
@Entity
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(length = 2000)
    private String annotation;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    private int confirmedRequests;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;
    @Column(length = 7000)
    private String description;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    @ManyToOne
    @JoinColumn(name = "initiator_id")
    private User initiator;
    @Embedded
    private Location location;
    private Boolean isPaid;
    private int participantLimit;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedOn;
    private Boolean isRequestModeration;
    @Enumerated(EnumType.STRING)
    private EventState state;
    @Column(length = 120)
    private String title;
    private Integer views;
}
