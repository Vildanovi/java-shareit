package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.enumerations.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking> findAllByItem_Owner(int ownerId);

    List<Booking> findAllByItem_OwnerOrderByStartDesc(int ownerId);

    List<Booking> findAllByItem_OwnerAndStatusOrderByStartDesc(int userId, BookingStatus state);

    List<Booking> findAllByItem_OwnerAndEndIsBeforeOrderByStartDesc(int userId, LocalDateTime currentDate);

    List<Booking> findAllByItem_OwnerAndStartAfterOrderByStartDesc(int userId, LocalDateTime currentDate);

    List<Booking> findAllByItem_OwnerAndEndIsAfterOrderByStartDesc(int userId, LocalDateTime currentDate);

    List<Booking> findAllByBooker_IdOrderByStartDesc(int userId);

    List<Booking> findAllByBooker_IdAndStatusOrderByStartDesc(int userId, BookingStatus state);

    List<Booking> findAllByBooker_IdAndEndIsBeforeOrderByStartDesc(int userId, LocalDateTime currentDate);

    List<Booking> findAllByBooker_IdAndItem_IdAndEndIsBeforeOrderByStartDesc(int userId, int itemId, LocalDateTime currentDate);

    List<Booking> findAllByBooker_IdAndStartAfterOrderByStartDesc(int userId, LocalDateTime currentDate);

    List<Booking> findAllByBooker_IdAndEndIsAfterOrderByStartDesc(int userId, LocalDateTime currentDate);

    List<Booking> findAllByItem_IdAndEndIsBeforeOrderByEndDesc(int itemId, LocalDateTime currentDate);

    List<Booking> findAllByItem_IdAndStartIsAfterOrderByStartAsc(int itemId, LocalDateTime currentDate);

    List<Booking> findAllByItem_IdAndItem_OwnerAndStartIsAfterOrderByStartAsc(int itemId, int ownerId, LocalDateTime currentDate);

    List<Booking> findAllByBooker_IdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(int userId, LocalDateTime startDate, LocalDateTime endDate);

    List<Booking> findAllByItem_OwnerAndStartIsBeforeAndEndIsAfterOrderByStartDesc(int ownerId, LocalDateTime startDate, LocalDateTime endDate);
}
