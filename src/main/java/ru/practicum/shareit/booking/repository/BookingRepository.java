package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.enumerations.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking> findAllByItem_Owner(int ownerId);

    List<Booking> findAllByItem_OwnerAndStatus(int userId, BookingStatus state);

    List<Booking> findAllByBookerId(int userId);

    List<Booking> findAllByBooker_IdAndStatus(int userId, BookingStatus state);
    List<Booking> findAllByBooker_IdAndEndBefore(int userId, LocalDateTime currentDate);
    List<Booking> findAllByBooker_IdAndStartAfter(int userId, LocalDateTime currentDate);
    List<Booking> findAllByBooker_IdAndEndIsAfter(int userId, LocalDateTime currentDate);
}
