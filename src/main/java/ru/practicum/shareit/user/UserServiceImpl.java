package ru.practicum.shareit.user;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto create(UserDto user) {
        return UserMapper.mapToUserDto(userRepository.save(UserMapper.mapToUser(user)));
    }

    @Override
    public UserDto update(long userId, UserDto user) {
        if (userRepository.existsByEmailIgnoreCase(user.getEmail())) {
            throw new ValidationException("email " + user.getEmail() + " already exists");
        }
        User updatedUser = UserMapper.updateUser(userRepository.findById(userId).orElseThrow(), user);
        return UserMapper.mapToUserDto(userRepository.save(updatedUser));
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserById(long userId) {
        return UserMapper.mapToUserDto(userRepository.findById(userId).orElseThrow());
    }

    @Override
    public void delete(long userId) {
        userRepository.deleteById(userId);
    }
}
