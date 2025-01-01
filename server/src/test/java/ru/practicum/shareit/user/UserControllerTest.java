package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {
    private static final String URL_PATH = "/users";

    @Autowired
    ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService service;

    private final UserDto userDto = new UserDto(1, "user", "user@mail.ru");

    @Test
    void createUserTest() throws Exception {
        when(service.create(any(UserDto.class))).thenReturn(userDto);

        mvc.perform(post(URL_PATH)
                        .content(mapper.writeValueAsString(userDto))
                        .header("X-Sharer-User-Id", userDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(userDto)));

        verify(service, times(1)).create(eq(userDto));
    }

    @Test
    void updateUserTest() throws Exception {
        userDto.setName("name");
        when(service.update(anyLong(), any(UserDto.class))).thenReturn(userDto);

        mvc.perform(patch(URL_PATH + "/" + userDto.getId())
                        .content(mapper.writeValueAsString(userDto))
                        .header("X-Sharer-User-Id", userDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(userDto)));

        verify(service, times(1)).update(eq(userDto.getId()), eq(userDto));

        userDto.setName("user");
    }

    @Test
    void getUserByIdTest() throws Exception {
        when(service.getUserById(anyLong())).thenReturn(userDto);

        mvc.perform(get(URL_PATH + "/" + userDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(userDto)));

        verify(service, times(1)).getUserById(eq(userDto.getId()));
    }

    @Test
    void deleteUserTest() throws Exception {
        mvc.perform(delete(URL_PATH + "/" + userDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(service, times(1)).delete(eq(userDto.getId()));
    }
}
