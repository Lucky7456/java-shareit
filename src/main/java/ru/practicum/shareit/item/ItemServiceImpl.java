package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.user.UserRepository;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public List<ItemDto> findAllUserItems(long userId) {
        return itemRepository.findAllByOwnerId(userId).stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    @Override
    public List<ItemDto> searchItemsBy(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.findAllByText(text).stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    @Override
    public ItemDto getItemById(long itemId) {
        return ItemMapper.mapToItemDto(itemRepository.findById(itemId).orElseThrow());
    }

    @Override
    @Transactional
    public ItemDto create(long userId, ItemDto itemDto) {
        Item item = ItemMapper.mapToItem(userRepository.findById(userId).orElseThrow(), itemDto);
        return ItemMapper.mapToItemDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public ItemDto update(long userId, long itemId, ItemDto itemDto) {
        Item item = itemRepository.findById(itemId).orElseThrow();
        if (item.getOwner().getId() != userId) {
            throw new ForbiddenException("insufficient authority");
        }
        Item updatedItem = ItemMapper.updateItem(item, itemDto);
        return ItemMapper.mapToItemDto(itemRepository.save(updatedItem));
    }
}
