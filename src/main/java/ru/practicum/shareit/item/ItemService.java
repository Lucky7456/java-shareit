package ru.practicum.shareit.item;

import java.util.List;

public interface ItemService {
    ItemDto create(long userId, ItemDto itemDto);

    ItemDto update(long userId, long itemId, ItemDto itemDto);

    ItemOwnerDto getItemById(long itemId);

    List<ItemOwnerDto> findAllByOwnerId(long ownerId);

    List<ItemDto> searchItemsBy(String text);

    CommentDto createComment(long bookerId, long itemId, CommentDto comment);
}
