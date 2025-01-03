package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ItemRequestServiceImplTest {
    private final ItemRequestService service;
    private final ItemRequestRepository requestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    private final LocalDateTime start = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);
    private final User user = new User(1, "user", "user@mail.ru");
    private final ItemRequest request = new ItemRequest(1L, "description", user, start);
    private final Item item = new Item(1L, "item", "description", true, user, null);
    private final ItemRequestDto requestDto = new ItemRequestDto(1, "description", 1, start, Collections.emptyList());

    @BeforeAll
    void setUp() {
        userRepository.save(user);
        requestRepository.save(request);
        itemRepository.save(item);
    }

    @Test
    void createRequestTest() {
        requestDto.setId(2L);
        requestDto.setItems(null);
        assertEquals(service.create(user.getId(), requestDto), requestDto);
        requestDto.setId(1L);
    }

    @Test
    void findByIdTest() {
        assertEquals(service.findById(request.getId()), requestDto);
    }

    @Test
    void findAllByUserIdTest() {
        assertEquals(service.findAllByUserId(request.getId()), List.of(requestDto));
    }

    @Test
    void findAllTest() {
        assertEquals(service.findAll(request.getId()), Collections.emptyList());
    }
}
