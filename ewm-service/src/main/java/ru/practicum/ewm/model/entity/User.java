package ru.practicum.ewm.model.entity;

import lombok.*;
import org.checkerframework.common.aliasing.qual.Unique;

import javax.persistence.*;
import javax.validation.constraints.Email;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Table(name = "users")
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Unique
    private String name;
    @Unique
    @Email
    private String email;

}
