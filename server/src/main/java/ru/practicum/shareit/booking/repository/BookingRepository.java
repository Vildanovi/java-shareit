package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.enumerations.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking> findAllByItem_Owner_Id(int ownerId, Pageable page);

    List<Booking> findAllByItem_IdIn(List<Integer> itemIds, Sort sort);

    List<Booking> findAllByItem_Owner_IdAndStatus(int userId, BookingStatus state, Pageable page);

    List<Booking> findAllByItem_Owner_IdAndEndIsBefore(int userId, LocalDateTime currentDate, Pageable page);

    List<Booking> findAllByItem_Owner_IdAndStartAfter(int userId, LocalDateTime currentDate, Pageable page);

    List<Booking> findAllByBooker_Id(int userId, Pageable page);

    List<Booking> findAllByBooker_IdAndStatus(int userId, BookingStatus state, Pageable page);

    List<Booking> findAllByBooker_IdAndEndIsBefore(int userId, LocalDateTime currentDate, Pageable page);

    Boolean existsByBooker_IdAndItem_IdAndEndIsBeforeOrderByStartDesc(int userId, int itemId, LocalDateTime currentDate);

    List<Booking> findAllByBooker_IdAndStartAfter(int userId, LocalDateTime currentDate, Pageable page);

    Booking findFirstByItem_IdAndStatusAndStartLessThanEqualOrderByEndDesc(int itemId, BookingStatus status, LocalDateTime currentDate);

    Booking findFirstByItem_IdAndStatusAndStartIsAfterOrderByStartAsc(Integer id, BookingStatus status, LocalDateTime start);

    List<Booking> findAllByBooker_IdAndStartIsBeforeAndEndIsAfter(int userId, LocalDateTime startDate, LocalDateTime endDate, Pageable page);

    List<Booking> findAllByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(int ownerId, LocalDateTime startDate, LocalDateTime endDate, Pageable page);
}
