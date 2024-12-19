package ru.practicum.shareit.item;

import java.util.List;

public interface ItemService {
    ItemDto create(long userId, ItemDto itemDto);

    ItemDto update(long userId, long itemId, ItemDto itemDto);

    ItemDto getItemById(long itemId);

    List<ItemDto> findAllUserItems(long userId);

    List<ItemDto> searchItemsBy(String text);
}
