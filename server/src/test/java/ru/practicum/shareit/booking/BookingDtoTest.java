package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.user.UserDto;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoTest {
    @Autowired
    private JacksonTester<BookingDto> jacksonTester;

    @Test
    void testBookingDto() throws Exception {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusSeconds(2);
        ItemDto itemDto = new ItemDto(1L, "item", "itemDto", true, 1L);
        UserDto userDto = new UserDto(1, "user", "user@dto");
        BookingDto bookingDto = new BookingDto(1, start, end, itemDto, userDto, BookingStatus.WAITING);

        JsonContent<BookingDto> json = jacksonTester.write(bookingDto);

        assertThat(json).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(json).extractingJsonPathStringValue("$.start").startsWith(start.truncatedTo(ChronoUnit.MICROS).toString());
        assertThat(json).extractingJsonPathStringValue("$.end").startsWith(end.truncatedTo(ChronoUnit.MICROS).toString());
        assertThat(json).extractingJsonPathNumberValue("$.item.id").isEqualTo(1);
        assertThat(json).extractingJsonPathStringValue("$.item.name").isEqualTo(itemDto.getName());
        assertThat(json).extractingJsonPathBooleanValue("$.item.available").isEqualTo(itemDto.getAvailable());
        assertThat(json).extractingJsonPathNumberValue("$.booker.id").isEqualTo(1);
        assertThat(json).extractingJsonPathStringValue("$.booker.email").isEqualTo(userDto.getEmail());
        assertThat(json).extractingJsonPathStringValue("$.status").isEqualTo(bookingDto.getStatus().name());
    }
}
