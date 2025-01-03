package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DuplicateEmailException;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserServiceImplTest {
    private final UserService service;
    private final UserRepository userRepository;

    private final UserDto userDto = new UserDto(1, "user", "user@mail.ru");
    private final UserDto newUser = new UserDto(2, "user", "new@mail.ru");
    private final User user = new User(1, "user", "user@mail.ru");

    @BeforeAll
    void setUp() {
        userRepository.save(user);
    }

    @Test
    void createUserTest() {
        assertEquals(service.create(newUser), newUser);
    }

    @Test
    void updateUserTest() {
        newUser.setId(1);
        assertEquals(service.update(user.getId(), newUser), newUser);
    }

    @Test
    void updateUserFailTest() {
        assertThrows(DuplicateEmailException.class, () -> service.update(user.getId(), userDto));
    }

    @Test
    void getUserByIdTest() {
        assertEquals(service.getUserById(user.getId()), userDto);
    }

    @Test
    void deleteUserTest() {
        service.delete(user.getId());
        assertThrows(NoSuchElementException.class, () -> service.getUserById(user.getId()));
    }
}
