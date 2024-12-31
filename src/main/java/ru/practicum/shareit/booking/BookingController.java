package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto create(@RequestHeader("X-Sharer-User-Id") long userId,
                             @Valid @RequestBody BookingCreateRequestDto bookingDto) {
        if (bookingDto.getStart().equals(bookingDto.getEnd())) {
            throw new BookingValidationException("start date can't be equal to end date");
        }
        return bookingService.create(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approve(@RequestHeader("X-Sharer-User-Id") long userId,
                              @PathVariable long bookingId,
                              @RequestParam boolean approved) {
        return bookingService.approve(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto findBookingById(@RequestHeader("X-Sharer-User-Id") long userId,
                                      @PathVariable long bookingId) {
        return bookingService.findBookingById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> findAllBookingsByBookerId(@RequestHeader("X-Sharer-User-Id") long bookerId,
                                                      @RequestParam(defaultValue = "ALL") BookingState state) {
        return bookingService.findAllBookingsByBookerId(bookerId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> findAllBookingsByOwnerId(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                                     @RequestParam(defaultValue = "ALL") BookingState state) {
        return bookingService.findAllBookingsByOwnerId(ownerId, state);
    }
}
