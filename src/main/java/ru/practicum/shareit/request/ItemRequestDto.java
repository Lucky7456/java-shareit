package ru.practicum.shareit.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.ItemSummaryDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class ItemRequestDto {
    private long id;
    @NotBlank
    private String description;
    private long requesterId;
    private LocalDateTime created;
    List<ItemSummaryDto> items;
}
