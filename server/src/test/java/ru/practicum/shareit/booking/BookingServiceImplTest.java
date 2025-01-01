package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.BookingValidationException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BookingServiceImplTest {
    private final BookingService service;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    private final LocalDateTime start = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);
    private final LocalDateTime end = start.plusSeconds(2).truncatedTo(ChronoUnit.MICROS);
    private final UserDto userDto = new UserDto(1, "user", "user@mail.ru");
    private final User user = new User(1, "user", "user@mail.ru");
    private final ItemDto itemDto = new ItemDto(1L, "item", "description", true, null);
    private final Item item = new Item(1L, "item", "description", true, user, null);
    private final BookingCreateRequestDto bookingCreateRequestDto = new BookingCreateRequestDto(1, start, end);
    private final BookingDto bookingDto = new BookingDto(1, start, end, itemDto, userDto, BookingStatus.WAITING);
    private final Booking booking = new Booking(1, start, end, item, user, BookingStatus.WAITING);

    @BeforeAll
    void setUp() {
        userRepository.save(user);
        itemRepository.save(item);
        bookingRepository.save(booking);
    }

    @Test
    void createBookingTest() {
        bookingDto.setId(2);
        BookingDto result = service.create(userDto.getId(), bookingCreateRequestDto);
        assertEquals(result, bookingDto);
        bookingDto.setId(1);
    }

    @Test
    void createBookingFailTest() {
        item.setAvailable(false);
        itemRepository.save(item);
        assertThrows(NoSuchElementException.class, () -> service.create(2, bookingCreateRequestDto));
        assertThrows(BookingValidationException.class, () -> service.create(1, bookingCreateRequestDto));
    }

    @Test
    void approveBookingTest() {
        BookingDto result = service.approve(userDto.getId(), booking.getId(), true);
        assertEquals(result.getStatus(), BookingStatus.APPROVED);
    }

    @Test
    void approveBookingFalseTest() {
        BookingDto result = service.approve(userDto.getId(), booking.getId(), false);
        assertEquals(result.getStatus(), BookingStatus.REJECTED);
    }

    @Test
    void approveBookingFailTest() {
        assertThrows(ForbiddenException.class, () -> service.approve(2, booking.getId(), true));
        service.approve(userDto.getId(), booking.getId(), false);
        assertThrows(ForbiddenException.class, () -> service.approve(userDto.getId(), booking.getId(), true));
    }

    @Test
    void findBookingByIdTest() {
        assertEquals(service.findBookingById(userDto.getId(), booking.getId()), bookingDto);
    }

    @Test
    void findBookingByIdFailTest() {
        assertThrows(NoSuchElementException.class, () -> service.findBookingById(1, 2));
        assertThrows(ForbiddenException.class, () -> service.findBookingById(2, booking.getId()));
    }

    @Test
    void findAllBookingsByOwnerIdTest() {
        List<BookingDto> result = service.findAllBookingsByOwnerId(userDto.getId(), BookingState.ALL);
        assertEquals(result, List.of(bookingDto));
    }

    @Test
    void findAllBookingsByBookerIdTest() {
        List<BookingDto> result = service.findAllBookingsByBookerId(userDto.getId(), BookingState.CURRENT);
        assertEquals(result, List.of(bookingDto));

        result = service.findAllBookingsByBookerId(userDto.getId(), BookingState.PAST);
        assertEquals(result, Collections.emptyList());

        result = service.findAllBookingsByBookerId(userDto.getId(), BookingState.FUTURE);
        assertEquals(result, Collections.emptyList());

        result = service.findAllBookingsByBookerId(userDto.getId(), BookingState.WAITING);
        assertEquals(result, List.of(bookingDto));

        result = service.findAllBookingsByBookerId(userDto.getId(), BookingState.REJECTED);
        assertEquals(result, Collections.emptyList());
    }
}
