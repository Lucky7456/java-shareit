package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository inMemoryUserRepository;

    public UserDto create(UserDto user) {
        return UserMapper.mapToUserDto(inMemoryUserRepository.create(UserMapper.mapToUser(user)));
    }

    public UserDto update(long userId, UserDto user) {
        return UserMapper.mapToUserDto(inMemoryUserRepository.update(userId, UserMapper.mapToUser(user)));
    }

    public UserDto getUserById(long userId) {
        return UserMapper.mapToUserDto(inMemoryUserRepository.getUserById(userId));
    }

    public void delete(long userId) {
        inMemoryUserRepository.delete(userId);
    }
}
