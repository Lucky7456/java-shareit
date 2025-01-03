package ru.practicum.shareit.booking;

import java.util.List;

public interface BookingService {
    BookingDto create(long userId, BookingCreateRequestDto bookingDto);

    BookingDto approve(long userId, long bookingId, boolean approved);

    BookingDto findBookingById(long ownerId, long bookingId);

    List<BookingDto> findAllBookingsByBookerId(long bookerId, BookingState state);

    List<BookingDto> findAllBookingsByOwnerId(long ownerId, BookingState state);
}
