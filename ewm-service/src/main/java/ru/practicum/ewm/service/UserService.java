package ru.practicum.ewm.service;

import ru.practicum.ewm.model.entity.User;

import java.util.List;

public interface UserService {

    User addUser(User user);

    User deleteUser(long userId);

    List<User> getUsers(long[] ids, int from, int size);

    User getUserById(long userId);
}
