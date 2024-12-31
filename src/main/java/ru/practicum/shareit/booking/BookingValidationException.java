package ru.practicum.shareit.booking;

public class BookingValidationException extends RuntimeException {
    public BookingValidationException(String message) {
        super(message);
    }
}
