package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.apache.bcel.generic.SWITCH;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingNewDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.enumerations.BookingState;
import ru.practicum.shareit.booking.enumerations.BookingStatus;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidationBadRequestException;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public Booking getBookingById(int bookingId, int userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Объект не найден: " + bookingId));
        if(booking.getBooker().getId() != userId && booking.getItem().getOwner() != userId) {
            throw new EntityNotFoundException("Недоступно так как пользователь не владельц или забронировавший: " + userId);
        }
        return booking;
    }

    @Override
    public Booking create(BookingNewDto bookingNewDto, int userId) {
        Item item = itemRepository.findById(bookingNewDto.getItemId())
                .orElseThrow(() -> new EntityNotFoundException("Объект не найден: " + bookingNewDto.getItemId()));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Объект не найден: " + userId));
        if(!item.getAvailable()) {
            throw new ValidationBadRequestException("Объект в данный момент не доступен: " + item.getId());
        }
        LocalDateTime start = bookingNewDto.getStart();
        LocalDateTime end = bookingNewDto.getEnd();
        if(end.isBefore(start) || end.isEqual(start)) {
            throw new ValidationBadRequestException("Дата завершения брони не может быть раньше начала");
        }
        if(item.getOwner() == userId) {
            throw new EntityNotFoundException("Объект не может забронировать владелец: " + userId);
        }
        Booking booking = BookingMapper.mapNewBookingToBooking(bookingNewDto, item, user);
        booking.setStatus(BookingStatus.WAITING);
        bookingRepository.save(booking);
        return booking;
    }

    @Override
    public List<Booking> getAllBookingByOwner(int ownerId) {
        return bookingRepository.findAllByItem_Owner(ownerId);
    }

    @Override
    public Booking approved(int bookingId, int ownerId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Объект не найден: " + bookingId));
        if(booking.getItem().getOwner() != ownerId) {
            throw new EntityNotFoundException("Подтверджающий бронирование не является владельцем: " + ownerId);
        }
        BookingStatus status = booking.getStatus();
        if(status.equals(BookingStatus.APPROVED) || status.equals(BookingStatus.REJECTED)) {
            throw new ValidationBadRequestException("Бронированию уже установлен статус подиверждения: " + status);
        }
        if(approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return bookingRepository.save(booking);
    }

    @Override
    public List<Booking> getAllBookingByUser(int userId, String state) {
        BookingState requestState = BookingState.valueOf(state);
        List<Booking> bookings = new ArrayList<>();
        switch (requestState) {
            case ALL:
                bookings = bookingRepository.findAllByBookerId(userId);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBooker_IdAndStatus(userId, BookingStatus.REJECTED);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBooker_IdAndStatus(userId, BookingStatus.WAITING);
                break;
            case PAST:
                bookings = bookingRepository.findAllByBooker_IdAndEndBefore(userId, LocalDateTime.now());
            case FUTURE:
                bookings = bookingRepository.findAllByBooker_IdAndStartAfter(userId, LocalDateTime.now());
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByBooker_IdAndEndIsAfter(userId, LocalDateTime.now());
                break;
        }
        return bookings;

    }


}
