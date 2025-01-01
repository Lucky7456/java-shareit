package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.QBooking;
import ru.practicum.shareit.exception.BookingValidationException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public List<ItemOwnerDto> findAllByOwnerId(long ownerId) {
        Iterable<Booking> bookings = bookingRepository.findAll(QBooking.booking.item.owner.id.eq(ownerId), QBooking.booking.start.desc());
        return itemRepository.findAllByOwnerId(ownerId).stream()
                .map(i -> {
                    List<Booking> itemBookings = StreamSupport.stream(bookings.spliterator(), false)
                            .filter(b -> Objects.equals(b.getItem().getId(), i.getId())).toList();
                    Booking lastBooking = itemBookings.stream()
                            .filter(b -> b.getStart().isBefore(LocalDateTime.now())).findFirst().orElse(null);
                    Booking nextBooking = itemBookings.reversed().stream()
                            .filter(b -> b.getStart().isAfter(LocalDateTime.now())).findFirst().orElse(null);
                    return ItemMapper.mapToItemOwnerDto(
                            i,
                            lastBooking != null ? lastBooking.getStart() : null,
                            nextBooking != null ? nextBooking.getStart() : null,
                            StreamSupport.stream(commentRepository.findAll(QComment.comment.item.id.eq(i.getId()))
                                    .spliterator(), false).map(CommentMapper::toDto).toList()
                    );
                })
                .toList();
    }

    @Override
    public List<ItemDto> searchItemsBy(String text) {
        return itemRepository.findAllByText(text).stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    @Override
    public ItemOwnerDto getItemById(long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow();
        List<CommentDto> comments = StreamSupport
                .stream(commentRepository.findAll(QComment.comment.item.id.eq(itemId)).spliterator(), false)
                .map(CommentMapper::toDto).toList();
        return ItemMapper.mapToItemOwnerDto(item, null, null, comments);
    }

    @Override
    @Transactional
    public ItemDto create(long userId, ItemDto itemDto) {
        User user = userRepository.findById(userId).orElseThrow();
        ItemRequest itemRequest = itemDto.getRequestId() != null ?
                itemRequestRepository.findById(itemDto.getRequestId()).orElseThrow() : null;
        return ItemMapper.mapToItemDto(itemRepository.save(ItemMapper.mapToItem(user, itemDto, itemRequest)));
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
