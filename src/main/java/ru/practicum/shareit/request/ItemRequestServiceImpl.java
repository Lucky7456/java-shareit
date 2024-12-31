package ru.practicum.shareit.request;

import com.querydsl.core.types.dsl.NumberPath;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemSummaryDto;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private static final NumberPath<Long> BY_REQUESTER_ID = QItemRequest.itemRequest.requester.id;
    private static final Sort SORT_BY_CREATED_DESC = Sort.by("created").descending();

    private final ItemRequestRepository requestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ItemRequestDto create(long userId, ItemRequestDto requestDto) {
        ItemRequest request = ItemRequestMapper.toRequest(userRepository.findById(userId).orElseThrow(), requestDto);
        return ItemRequestMapper.toDto(requestRepository.save(request), null);
    }

    @Override
    public ItemRequestDto findById(long userId, long requestId) {
        ItemRequest request = requestRepository.findById(requestId).orElseThrow();
        List<ItemSummaryDto> items = itemRepository.findAllByRequestId(requestId);
        return ItemRequestMapper.toDto(requestRepository.save(request), items);
    }

    @Override
    public List<ItemRequestDto> findAll(long userId) {
        return ItemRequestMapper.toDto(requestRepository.findAll(BY_REQUESTER_ID.ne(userId), SORT_BY_CREATED_DESC));
    }

    @Override
    public List<ItemRequestDto> findAllByUserId(long userId) {
        Iterable<ItemRequest> requests = requestRepository.findAll(BY_REQUESTER_ID.eq(userId), SORT_BY_CREATED_DESC);
        return StreamSupport.stream(requests.spliterator(), false)
                .map(request -> ItemRequestMapper.toDto(request, itemRepository.findAllByRequestId(request.getId())))
                .toList();
    }
}
