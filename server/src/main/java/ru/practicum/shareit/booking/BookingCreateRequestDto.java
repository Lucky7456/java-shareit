package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingCreateRequestDto {
    private long itemId;
    private LocalDateTime start;
    private LocalDateTime end;
}
