package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingNewDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.enumerations.BookingState;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {

    Booking getBookingById(int bookingId, int userId);

    Booking create(BookingNewDto bookingNewDto, int userId);

    List<Booking> getAllBookingByOwner(int ownerId);

    Booking approved(int bookingId, int ownerId, boolean approved);

    List<Booking> getAllBookingByUser(int userId, String state);
}
