package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.user.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public List<ItemDto> findAllUserItems(long userId) {
        return itemRepository.findAll().stream()
                .filter(i -> i.getOwnerId() == userId)
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    public List<ItemDto> searchItemsBy(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.findAll().stream()
                .filter(i -> i.getName() != null && i.getDescription() != null)
                .filter(Item::getAvailable)
                .filter(i -> i.getName().toLowerCase().contains(text.toLowerCase()) ||
                        i.getDescription().toLowerCase().contains(text.toLowerCase()))
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    public ItemDto getItemById(long itemId) {
        return ItemMapper.mapToItemDto(itemRepository.findOneById(itemId));
    }

    public ItemDto create(long userId, ItemDto itemDto) {
        if (userRepository.getUserById(userId) == null) {
            throw new NoSuchElementException("user with id = " + userId + " not found");
        }
        Item item = ItemMapper.mapToItem(itemDto);
        item.setOwnerId(userId);
        return ItemMapper.mapToItemDto(itemRepository.create(item));
    }

    public ItemDto update(long userId, long itemId, ItemDto itemDto) {
        if (itemRepository.findOneById(itemId).getOwnerId() != userId) {
            throw new ForbiddenException("insufficient authority");
        }
        return ItemMapper.mapToItemDto(itemRepository.update(itemId, ItemMapper.mapToItem(itemDto)));
    }
}
