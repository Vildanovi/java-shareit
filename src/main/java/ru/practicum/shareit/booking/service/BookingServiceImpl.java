package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingNewDto;
import ru.practicum.shareit.booking.enumerations.BookingState;
import ru.practicum.shareit.booking.enumerations.BookingStatus;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidationBadRequestException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public Booking getBookingById(int bookingId, int userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Объект не найден: " + bookingId));
        if (booking.getBooker().getId() != userId && booking.getItem().getOwner() != userId) {
            throw new EntityNotFoundException("Недоступно так как пользователь не владельц или забронировавший: " + userId);
        }
        return booking;
    }

    @Override
    @Transactional
    public Booking create(BookingNewDto bookingNewDto, int userId) {
        Item item = itemRepository.findById(bookingNewDto.getItemId())
                .orElseThrow(() -> new EntityNotFoundException("Объект не найден: " + bookingNewDto.getItemId()));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Объект не найден: " + userId));
        if (!item.getAvailable()) {
            throw new ValidationBadRequestException("Объект в данный момент не доступен: " + item.getId());
        }
        LocalDateTime start = bookingNewDto.getStart();
        LocalDateTime end = bookingNewDto.getEnd();
        if (end.isBefore(start) || end.isEqual(start)) {
            throw new ValidationBadRequestException("Дата завершения брони не может быть раньше начала");
        }
        if (item.getOwner() == userId) {
            throw new EntityNotFoundException("Объект не может забронировать владелец: " + userId);
        }
        Booking booking = BookingMapper.mapNewBookingToBooking(bookingNewDto, item, user);
        booking.setStatus(BookingStatus.WAITING);
        bookingRepository.save(booking);
        return booking;
    }

    @Override
    public List<Booking> getAllBookingByOwner(int ownerId, String state) {
        BookingState bookingState;
        LocalDateTime currentDateTime = LocalDateTime.now();
        try {
            bookingState = BookingState.valueOf(state);
        } catch (RuntimeException e) {
            throw new ValidationBadRequestException("Unknown state: UNSUPPORTED_STATUS");
        }
        userRepository.findById(ownerId)
                .orElseThrow(() -> new EntityNotFoundException("Объект не найден: " + ownerId));
        List<Booking> bookings;
        switch (bookingState) {
            case ALL:
                bookings = bookingRepository.findAllByItem_OwnerOrderByStartDesc(ownerId);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByItem_OwnerAndStatusOrderByStartDesc(ownerId, BookingStatus.REJECTED);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByItem_OwnerAndStatusOrderByStartDesc(ownerId, BookingStatus.WAITING);
                break;
            case PAST:
                bookings = bookingRepository.findAllByItem_OwnerAndEndIsBeforeOrderByStartDesc(ownerId, currentDateTime);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByItem_OwnerAndStartAfterOrderByStartDesc(ownerId, currentDateTime);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByItem_OwnerAndStartIsBeforeAndEndIsAfterOrderByStartDesc(ownerId, currentDateTime, currentDateTime);
                break;
            default:
                throw new ValidationBadRequestException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookings;
    }

    @Override
    public List<Booking> getAllBookingByUser(int userId,String state) {
        BookingState bookingState;
        try {
            bookingState = BookingState.valueOf(state);
        } catch (RuntimeException e) {
            throw new ValidationBadRequestException("Unknown state: UNSUPPORTED_STATUS");
        }
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Объект не найден: " + userId));
        List<Booking> bookings = new ArrayList<>();
        LocalDateTime currentDateTime = LocalDateTime.now();
        switch (bookingState) {
            case ALL:
                bookings = bookingRepository.findAllByBooker_IdOrderByStartDesc(userId);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBooker_IdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBooker_IdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
                break;
            case PAST:
                bookings = bookingRepository.findAllByBooker_IdAndEndIsBeforeOrderByStartDesc(userId, currentDateTime);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBooker_IdAndStartAfterOrderByStartDesc(userId, currentDateTime);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByBooker_IdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(userId, currentDateTime, currentDateTime);
                break;
        }
        return bookings;
    }

    @Override
    @Transactional
    public Booking approved(int bookingId, int ownerId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Объект не найден: " + bookingId));
        if (booking.getItem().getOwner() != ownerId) {
            throw new EntityNotFoundException("Подтверджающий бронирование не является владельцем: " + ownerId);
        }
        BookingStatus status = booking.getStatus();
        if (status.equals(BookingStatus.APPROVED) || status.equals(BookingStatus.REJECTED)) {
            throw new ValidationBadRequestException("Бронированию уже установлен статус подиверждения: " + status);
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return bookingRepository.save(booking);
    }
}