package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;

public interface BookingService {

    Booking getBookingById(int id);

    BookingResponseDto saveBooking(BookingDto bookingDto, int userId);
}
