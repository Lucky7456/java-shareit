package ru.practicum.shareit.user;

import jakarta.validation.ValidationException;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class InMemoryUserRepository implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private static long nextId = 1;

    @Override
    public User create(User user) {
        if (isDuplicateEmail(user.getEmail())) {
            throw new ValidationException("Duplicate email: " + user.getEmail());
        }
        long id = generateId();
        user.setId(id);
        users.put(id, user);
        return user;
    }

    @Override
    public User update(long userId, User user) {
        User userToUpdate = users.get(userId);
        if (user.getName() != null && !user.getName().isBlank()) {
            userToUpdate.setName(user.getName());
        }
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            if (isDuplicateEmail(user.getEmail())) {
                throw new ValidationException("Duplicate email: " + user.getEmail());
            }
            userToUpdate.setEmail(user.getEmail());
        }
        users.put(userId, userToUpdate);
        return userToUpdate;
    }

    @Override
    public User getUserById(long userId) {
        return users.get(userId);
    }

    @Override
    public void delete(long userId) {
        users.remove(userId);
    }

    private boolean isDuplicateEmail(String email) {
        return users.values().stream().anyMatch(u -> u.getEmail().equals(email));
    }

    private long generateId() {
        return nextId++;
    }
}
