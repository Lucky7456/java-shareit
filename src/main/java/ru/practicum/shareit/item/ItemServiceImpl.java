package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public List<ItemOwnerDto> findAllByOwnerId(long ownerId) {
        Iterable<Booking> bookings = bookingRepository.findAll(QBooking.booking.item.owner.id.eq(ownerId), QBooking.booking.start.desc());
        return itemRepository.findAllByOwnerId(ownerId).stream()
                .map(i -> {
                    List<Booking> itemBookings = StreamSupport.stream(bookings.spliterator(),false)
                            .filter(b -> b.getItem().getId() == i.getId()).toList();
                    Booking lastBooking = itemBookings.stream()
                            .filter(b -> b.getStart().isBefore(LocalDateTime.now())).findFirst().orElse(null);
                    Booking nextBooking = itemBookings.reversed().stream()
                            .filter(b -> b.getStart().isAfter(LocalDateTime.now())).findFirst().orElse(null);
                    return ItemMapper.mapToItemOwnerDto(
                            i,
                            lastBooking != null ? lastBooking.getStart() : null,
                            nextBooking != null ? nextBooking.getStart() : null,
                            commentRepository.findByItemId(i.getId()).stream().map(CommentMapper::toDto).toList()
                    );
                })
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
    public ItemOwnerDto getItemById(long itemId) {
        return ItemMapper.mapToItemOwnerDto(itemRepository.findById(itemId).orElseThrow(), null,
                null, commentRepository.findByItemId(itemId).stream().map(CommentMapper::toDto).toList());
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

    @Override
    @Transactional
    public CommentDto createComment(long bookerId, long itemId, CommentDto commentDto) {
        Booking booking = bookingRepository.findOneByBookerIdAndItemIdAndStatusAndEndBefore(bookerId, itemId,
                BookingStatus.APPROVED, LocalDateTime.now()).orElseThrow(() -> new BookingValidationException(""));
        Comment comment = CommentMapper.toComment(booking.getBooker(), booking.getItem(), commentDto);
        return CommentMapper.toDto(commentRepository.save(comment));
    }
}
