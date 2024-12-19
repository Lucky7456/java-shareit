package ru.practicum.shareit.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ItemRequestDto {
    private String description;
    private long requesterId;
    private LocalDateTime created;
}
