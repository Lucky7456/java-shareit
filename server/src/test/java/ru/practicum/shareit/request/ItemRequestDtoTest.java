package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemRequestDtoTest {
    @Autowired
    private JacksonTester<ItemRequestDto> tester;

    @Test
    void itemRequestDtoTest() throws IOException {
        LocalDateTime start = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        JsonContent<ItemRequestDto> json = tester.write(new ItemRequestDto(1L, "item", 1L, start, null));

        assertThat(json).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(json).extractingJsonPathStringValue("$.description").isEqualTo("item");
        assertThat(json).extractingJsonPathNumberValue("$.requesterId").isEqualTo(1);
        assertThat(json).extractingJsonPathStringValue("$.created").isEqualTo(start.toString());
    }
}
