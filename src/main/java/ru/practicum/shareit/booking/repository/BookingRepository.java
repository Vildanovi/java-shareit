package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.enumerations.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking> findAllByItem_Owner_Id(int ownerId);

    List<Booking> findAllByItem_Owner_Id(int ownerId, Sort sort);

    List<Booking> findAllByItem_Owner_IdAndStatusOrderByStartDesc(int userId, BookingStatus state);

    List<Booking> findAllByItem_Owner_IdAndEndIsBeforeOrderByStartDesc(int userId, LocalDateTime currentDate);

    List<Booking> findAllByItem_Owner_IdAndStartAfterOrderByStartDesc(int userId, LocalDateTime currentDate);

    List<Booking> findAllByBooker_IdOrderByStartDesc(int userId);

    List<Booking> findAllByBooker_IdAndStatusOrderByStartDesc(int userId, BookingStatus state);

    List<Booking> findAllByBooker_IdAndEndIsBeforeOrderByStartDesc(int userId, LocalDateTime currentDate);

    List<Booking> findAllByBooker_IdAndItem_IdAndEndIsBeforeOrderByStartDesc(int userId, int itemId, LocalDateTime currentDate);

    List<Booking> findAllByBooker_IdAndStartAfterOrderByStartDesc(int userId, LocalDateTime currentDate);

    List<Booking> findAllByItem_Owner_IdAndStartIsBeforeOrderByEndDesc(int ownerId, LocalDateTime currentDate);

    List<Booking> findAllByItem_IdAndItem_Owner_IdAndStartIsAfterOrderByStartAsc(int itemId, int ownerId, LocalDateTime currentDate);

    List<Booking> findAllByBooker_IdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(int userId, LocalDateTime startDate, LocalDateTime endDate);

    List<Booking> findAllByItem_Owner_IdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(int ownerId, LocalDateTime startDate, LocalDateTime endDate);
}
