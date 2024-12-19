package ru.practicum.shareit.item;

import java.util.List;

public interface ItemRepository {
    List<Item> findAll();

    Item findOneById(long itemId);

    Item create(Item item);

    Item update(long itemId, Item item);

    void delete(long itemId);
}
