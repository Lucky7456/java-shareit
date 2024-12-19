package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class InMemoryItemRepository implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private static long nextId = 1;

    @Override
    public List<Item> findAll() {
        return items.values().stream().toList();
    }

    @Override
    public Item findOneById(long itemId) {
        return items.get(itemId);
    }

    @Override
    public Item create(Item item) {
        long id = generateId();
        item.setId(id);
        items.put(id, item);
        return items.get(id);
    }

    @Override
    public Item update(long itemId, Item item) {
        Item itemToUpdate = items.get(itemId);
        if (item.getName() != null && !item.getName().isBlank()) {
            itemToUpdate.setName(item.getName());
        }
        if (item.getDescription() != null && !item.getDescription().isBlank()) {
            itemToUpdate.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null && item.getAvailable() != itemToUpdate.getAvailable()) {
            itemToUpdate.setAvailable(item.getAvailable());
        }
        items.put(itemId, item);
        return itemToUpdate;
    }

    @Override
    public void delete(long itemId) {
        items.remove(itemId);
    }

    private static Long generateId() {
        return nextId++;
    }
}
