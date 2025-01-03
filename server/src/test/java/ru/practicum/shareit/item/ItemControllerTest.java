package ru.practicum.shareit.item;

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
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {
    private static final String URL_PATH = "/items";

    @Autowired
    ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ItemService service;

    private final LocalDateTime start = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);
    private final LocalDateTime end = LocalDateTime.now().plusSeconds(2).truncatedTo(ChronoUnit.MICROS);
    private final User user = new User(1, "user", "user@mail.ru");
    private final ItemDto itemDto = new ItemDto(1L, "name", "description", true, 1L);
    private final CommentDto commentDto = new CommentDto(1, "comment", user.getName(), start);
    private final ItemOwnerDto itemOwnerDto =
            new ItemOwnerDto(1L, 1L, "name", "text", false, start, end, List.of(commentDto));


    @Test
    void createItemTest() throws Exception {
        when(service.create(anyLong(), any(ItemDto.class))).thenReturn(itemDto);

        mvc.perform(post(URL_PATH)
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));

        verify(service, times(1)).create(eq(user.getId()), eq(itemDto));
    }

    @Test
    void updateItemTest() throws Exception {
        itemDto.setAvailable(false);
        when(service.update(anyLong(), anyLong(), any(ItemDto.class))).thenReturn(itemDto);

        mvc.perform(patch(URL_PATH + "/" + itemDto.getId())
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));

        verify(service, times(1)).update(eq(user.getId()), eq(itemDto.getId()), eq(itemDto));
    }

    @Test
    void getItemByIdTest() throws Exception {
        when(service.getItemById(anyLong())).thenReturn(itemOwnerDto);

        mvc.perform(get(URL_PATH + "/" + itemDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemOwnerDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemOwnerDto.getName())))
                .andExpect(jsonPath("$.description", is(itemOwnerDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemOwnerDto.getAvailable())));

        verify(service, times(1)).getItemById(eq(itemDto.getId()));
    }

    @Test
    void findAllByOwnerIdTest() throws Exception {
        when(service.findAllByOwnerId(anyLong())).thenReturn(List.of(itemOwnerDto));

        mvc.perform(get(URL_PATH)
                        .header("X-Sharer-User-Id", user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemOwnerDto))));

        verify(service, times(1)).findAllByOwnerId(eq(user.getId()));
    }

    @Test
    void searchItemTest() throws Exception {
        when(service.searchItemsBy(anyString())).thenReturn(List.of(itemDto));

        mvc.perform(get(URL_PATH + "/search")
                        .param("text", "description")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemDto))));

        verify(service, times(1)).searchItemsBy(eq("description"));
    }

    @Test
    void createCommentTest() throws Exception {
        when(service.createComment(anyLong(), anyLong(), any(CommentDto.class))).thenReturn(commentDto);

        mvc.perform(post(URL_PATH + "/" + itemDto.getId() + "/comment")
                        .content(mapper.writeValueAsString(commentDto))
                        .header("X-Sharer-User-Id", user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(commentDto)));

        verify(service, times(1)).createComment(eq(user.getId()), eq(itemDto.getId()), eq(commentDto));
    }
}
