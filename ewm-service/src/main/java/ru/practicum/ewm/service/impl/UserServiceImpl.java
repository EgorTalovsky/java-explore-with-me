package ru.practicum.ewm.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.exception.EntityAlreadyExistException;
import ru.practicum.ewm.exception.IncorrectFieldException;
import ru.practicum.ewm.exception.NoDataException;
import ru.practicum.ewm.model.entity.User;
import ru.practicum.ewm.repository.UserRepository;
import ru.practicum.ewm.service.UserService;

import java.util.List;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public User addUser(User user) {
        checkUserFields(user);
        return userRepository.save(user);
    }

    public List<User> getUsers(long[] ids, int from, int size) {
        Pageable page = PageRequest.of(from / size, size);
        List<User> users;
        if (ids == null)
            users = userRepository.getAllUsers(page);
        else
            users = userRepository.getUsersByIds(ids, page);
        return users;
    }

    public User deleteUser(long userId) {
        User user = getUserById(userId);
        userRepository.delete(user);
        return user;
    }

    public User getUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(()
                -> new NoDataException("Пользователь не найден"));
    }

    private void checkUserFields(User user) {
        if (user.getName().length() < 2 || user.getName().length() > 250) {
            throw new IncorrectFieldException("Имя должно быть не менее 2 символов, но не более 250");
        }
        if (user.getEmail().length() < 6 || user.getEmail().length() > 254) {
            throw new IncorrectFieldException("Почта должно быть не менее 6 символов, но не более 254");
        }
        if (userRepository.findUserByName(user.getName()).isPresent()) {
            throw new EntityAlreadyExistException("Пользователь с таким ником уже зарегистрирован");
        }
    }
}
