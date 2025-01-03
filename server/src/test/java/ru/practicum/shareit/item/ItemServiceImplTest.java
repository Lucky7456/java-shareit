package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ItemServiceImplTest {
    private final ItemService service;
    private final ItemRequestRepository itemRequestRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    private final LocalDateTime start = LocalDateTime.now().minusSeconds(2).truncatedTo(ChronoUnit.MICROS);
    private final LocalDateTime end = start.plusSeconds(2).truncatedTo(ChronoUnit.MICROS);
    private final User user = new User(1, "user", "user@mail.ru");
    private final ItemRequest itemRequest = new ItemRequest(1L, "description", user, start);
    private final ItemDto itemDto = new ItemDto(1L, "item", "description", true, 1L);
    private final Item item = new Item(1L, "item", "description", true, user, itemRequest);
    private final CommentDto commentDto = new CommentDto(1, "comment", user.getName(), start);
    private final Comment comment = new Comment(1L, "comment", item, user, start);
    private final Booking booking = new Booking(1, start, end, item, user, BookingStatus.APPROVED);
    private final ItemOwnerDto itemOwnerDto =
            new ItemOwnerDto(1L, 1L, "item", "description", true, null, null, List.of(commentDto));

    @BeforeAll
    void setUp() {
        userRepository.save(user);
        itemRequestRepository.save(itemRequest);
        itemRepository.save(item);
        commentRepository.save(comment);
        bookingRepository.save(booking);
    }

    @Test
    void createItemTest() {
        itemDto.setId(2L);
        itemDto.setRequestId(null);
        ItemDto item = service.create(user.getId(), itemDto);
        assertEquals(item, itemDto);
        itemDto.setId(1L);
    }

    @Test
    void createItemFailTest() {
        assertThrows(NoSuchElementException.class,
                () -> service.create(1, new ItemDto(1L, "", "", false, 2L)));
    }

    @Test
    void updateItemTest() {
        itemDto.setAvailable(false);
        ItemDto item = service.update(user.getId(), itemDto.getId(), itemDto);
        assertEquals(item, itemDto);
    }

    @Test
    void updateItemFailTest() {
        assertThrows(ForbiddenException.class, () -> service.update(2, itemDto.getId(), itemDto));
    }

    @Test
    void getItemByIdTest() {
        assertEquals(service.getItemById(itemDto.getId()), itemOwnerDto);
    }

    @Test
    void findAllByOwnerIdTest() {
        assertEquals(service.findAllByOwnerId(user.getId()).getFirst().getUserId(), itemOwnerDto.getUserId());
    }

    @Test
    void searchItemsByTextTest() {
        assertEquals(service.searchItemsBy("item"), List.of(itemDto));
    }

    @Test
    void createCommentTest() {
        assertEquals(service.createComment(user.getId(), itemDto.getId(), commentDto), commentDto);
    }
}
