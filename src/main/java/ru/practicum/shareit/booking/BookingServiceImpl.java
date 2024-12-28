package ru.practicum.shareit.booking;

import com.querydsl.core.types.dsl.BooleanExpression;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public BookingDto create(long userId, BookingCreateRequestDto bookingDto) {
        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow();
        if (!item.getAvailable()) {
            throw new ValidationException("item unavailable for booking");
        }
        Booking booking = BookingMapper.toBooking(bookingDto, userRepository.findById(userId).orElseThrow(), item);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDto approve(long userId, long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow();
        if (booking.getItem().getOwner().getId() != userId || booking.getStatus() != BookingStatus.WAITING) {
            throw new ForbiddenException("insufficient authority");
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto findBookingById(long userId, long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow();
        if (booking.getItem().getOwner().getId() != userId && booking.getBooker().getId() != userId) {
            throw new ForbiddenException("insufficient authority");
        }
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> findAllBookingsByBookerId(long bookerId, BookingState state) {
        BooleanExpression byBookerAndState = QBooking.booking.booker.id.eq(bookerId).and(byState(state));
        return BookingMapper.toBookingDto(bookingRepository.findAll(byBookerAndState, QBooking.booking.start.desc()));
    }

    @Override
    public List<BookingDto> findAllBookingsByOwnerId(long ownerId, BookingState state) {
        BooleanExpression byOwnerAndState = QBooking.booking.item.owner.id.eq(ownerId).and(byState(state));
        return BookingMapper.toBookingDto(bookingRepository.findAll(byOwnerAndState, QBooking.booking.start.desc()));
    }

    private static BooleanExpression byState(BookingState state) {
        return switch (state) {
            case ALL -> null;
            case CURRENT -> QBooking.booking.start.before(LocalDateTime.now())
                    .and(QBooking.booking.end.after(LocalDateTime.now()));
            case PAST -> QBooking.booking.end.before(LocalDateTime.now());
            case FUTURE -> QBooking.booking.start.after(LocalDateTime.now());
            case WAITING -> QBooking.booking.status.eq(BookingStatus.WAITING);
            case REJECTED -> QBooking.booking.status.eq(BookingStatus.REJECTED);
        };
    }
}
