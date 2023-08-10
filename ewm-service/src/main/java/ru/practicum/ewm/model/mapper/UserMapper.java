package ru.practicum.ewm.model.mapper;

import ru.practicum.ewm.model.dto.UserDto;
import ru.practicum.ewm.model.entity.User;

public class UserMapper {

    public static UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

}
