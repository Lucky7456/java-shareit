package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {
    private static final String URL_PATH = "/requests";

    @Autowired
    ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ItemRequestService service;

    private final LocalDateTime start = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);
    private final User user = new User(1, "user", "user@mail.ru");
    private final ItemRequestDto requestDto = new ItemRequestDto(1, "text", 1, start, null);
    private final ItemRequestDto requestDtoWithSummary = new ItemRequestDto(1, "text", 1, start, Collections.emptyList());

    @Test
    void createRequestTest() throws Exception {
        when(service.create(anyLong(), any(ItemRequestDto.class))).thenReturn(requestDto);

        mvc.perform(post(URL_PATH)
                        .content(mapper.writeValueAsString(requestDto))
                        .header("X-Sharer-User-Id", user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(requestDto)));

        verify(service, times(1)).create(eq(user.getId()), eq(requestDto));
    }

    @Test
    void findByRequestIdTest() throws Exception {
        when(service.findById(anyLong())).thenReturn(requestDtoWithSummary);

        mvc.perform(get(URL_PATH + "/" + requestDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(requestDtoWithSummary)));

        verify(service, times(1)).findById(eq(user.getId()));
    }

    @Test
    void findAllByUserIdTest() throws Exception {
        when(service.findAllByUserId(anyLong())).thenReturn(List.of(requestDtoWithSummary));

        mvc.perform(get(URL_PATH)
                        .header("X-Sharer-User-Id", user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(requestDtoWithSummary))));

        verify(service, times(1)).findAllByUserId(eq(user.getId()));
    }

    @Test
    void findAllTest() throws Exception {
        when(service.findAll(anyLong())).thenReturn(List.of(requestDto));

        mvc.perform(get(URL_PATH + "/all")
                        .header("X-Sharer-User-Id", user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(requestDto))));

        verify(service, times(1)).findAll(eq(user.getId()));
    }
}
