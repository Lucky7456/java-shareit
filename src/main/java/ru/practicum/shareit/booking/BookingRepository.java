package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.time.LocalDateTime;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long>, QuerydslPredicateExecutor<Booking> {
    Optional<Booking> findOneByBookerIdAndItemIdAndStatusAndEndBefore(long bookerId, long itemId, BookingStatus status, LocalDateTime now);
}
