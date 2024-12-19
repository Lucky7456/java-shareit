package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserDto create(UserDto user) {
        return UserMapper.mapToUserDto(userRepository.create(UserMapper.mapToUser(user)));
    }

    public UserDto update(long userId, UserDto user) {
        return UserMapper.mapToUserDto(userRepository.update(userId, UserMapper.mapToUser(user)));
    }

    public UserDto getUserById(long userId) {
        return UserMapper.mapToUserDto(userRepository.getUserById(userId));
    }

    public void delete(long userId) {
        userRepository.delete(userId);
    }
}
