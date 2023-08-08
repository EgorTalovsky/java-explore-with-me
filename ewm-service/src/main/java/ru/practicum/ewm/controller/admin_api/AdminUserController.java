package ru.practicum.ewm.controller.admin_api;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.model.mapper.UserMapper;
import ru.practicum.ewm.model.entity.User;
import ru.practicum.ewm.model.dto.UserDto;
import ru.practicum.ewm.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping("/admin/users")
public class AdminUserController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto addUser(@RequestBody @Valid UserDto userDto) {
        User user = User.builder()
                .email(userDto.getEmail())
                .name(userDto.getName())
                .build();
        return UserMapper.toUserDto(userService.addUser(user));
    }

    @GetMapping
    public List<UserDto> getAllUsers(@RequestParam(required = false) long[] ids,
                                     @RequestParam(defaultValue = "0") @Min(0) int from,
                                     @RequestParam(defaultValue = "10") @Min(0) int size) {
        return userService.getUsers(ids, from, size).stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public UserDto deleteUserById(@PathVariable long userId) {
        return UserMapper.toUserDto(userService.deleteUser(userId));
    }
}
