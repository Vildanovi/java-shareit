package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingNewDto;
import ru.practicum.shareit.booking.model.Booking;
import java.util.List;

public interface BookingService {

    Booking getBookingById(int bookingId, int userId);

    Booking create(BookingNewDto bookingNewDto, int userId);

    List<Booking> getAllBookingByOwner(int ownerId, String state, int from, int size);

    Booking approved(int bookingId, int ownerId, boolean approved);

    List<Booking> getAllBookingByUser(int userId, String state, int from, int size);
}
