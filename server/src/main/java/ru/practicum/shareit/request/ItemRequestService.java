package ru.practicum.shareit.request;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto create(long userId, ItemRequestDto itemRequestDto);

    ItemRequestDto findById(long requestId);

    List<ItemRequestDto> findAll(long userId);

    List<ItemRequestDto> findAllByUserId(long userId);
}
