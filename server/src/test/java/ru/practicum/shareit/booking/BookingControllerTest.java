package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    private static final String URL_PATH = "/bookings";

    @Autowired
    ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private BookingService service;

    private final LocalDateTime start = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);
    private final LocalDateTime end = start.plusSeconds(2).truncatedTo(ChronoUnit.MICROS);
    private final BookingCreateRequestDto bookingCreateRequestDto = new BookingCreateRequestDto(1L, start, end);
    private final ItemDto itemDto = new ItemDto(1L, "name", "description", true, 1L);
    private final User user = new User(1, "user", "user@mail.ru");
    private final UserDto userDto = new UserDto(1, "user", "user@mail.ru");
    private final BookingDto bookingDto = new BookingDto(1, start, end, itemDto, userDto, BookingStatus.WAITING);

    @Test
    void createBookingTest() throws Exception {
        when(service.create(anyLong(), any(BookingCreateRequestDto.class))).thenReturn(bookingDto);

        mvc.perform(post(URL_PATH)
                        .content(mapper.writeValueAsString(bookingCreateRequestDto))
                        .header("X-Sharer-User-Id", user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", notNullValue()))
                .andExpect(jsonPath("$.end", notNullValue()))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(bookingDto.getItem().getName())))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.booker.name", is(bookingDto.getBooker().getName())))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().name())));

        verify(service, times(1)).create(eq(user.getId()), eq(bookingCreateRequestDto));
    }

    @Test
    void approveBookingTest() throws Exception {
        boolean approved = true;
        bookingDto.setStatus(BookingStatus.APPROVED);

        when(service.approve(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingDto);

        mvc.perform(patch(URL_PATH + "/" + user.getId())
                        .param("approved", String.valueOf(approved))
                        .header("X-Sharer-User-Id", user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().name())));

        verify(service, times(1)).approve(eq(userDto.getId()), eq(bookingDto.getId()), eq(approved));
    }

    @Test
    void findBookingByIdTest() throws Exception {
        when(service.findBookingById(anyLong(), anyLong())).thenReturn(bookingDto);

        mvc.perform(get(URL_PATH + "/" + user.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class));

        verify(service, times(1)).findBookingById(eq(userDto.getId()), eq(bookingDto.getId()));
    }

    @Test
    void findAllBookingsByBookerIdTest() throws Exception {
        when(service.findAllBookingsByBookerId(anyLong(), any(BookingState.class))).thenReturn(List.of(bookingDto));

        mvc.perform(get(URL_PATH)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(bookingDto))));

        verify(service, times(1)).findAllBookingsByBookerId(eq(userDto.getId()), eq(BookingState.ALL));
    }

    @Test
    void findAllBookingsByOwnerIdTest() throws Exception {
        when(service.findAllBookingsByOwnerId(anyLong(), any(BookingState.class))).thenReturn(List.of(bookingDto));

        mvc.perform(get(URL_PATH + "/owner")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(bookingDto))));

        verify(service, times(1)).findAllBookingsByOwnerId(eq(userDto.getId()), eq(BookingState.ALL));
    }
}
