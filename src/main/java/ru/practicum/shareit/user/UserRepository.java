package ru.practicum.shareit.user;

public interface UserRepository {
    User create(User user);

    User update(long userId, User user);

    User getUserById(long userId);

    void delete(long userId);
}
