package ru.practicum.shareit.user;

public interface UserService {
    UserDto create(UserDto userDto);

    UserDto update(long id, UserDto userDto);

    UserDto getUserById(long id);

    void delete(long id);
}
