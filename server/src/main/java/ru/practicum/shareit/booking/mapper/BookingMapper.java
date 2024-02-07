package ru.practicum.shareit.booking.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.dto.BookingNewDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class BookingMapper {

    public BookingForItemDto mapBookingToBookingForItem(Booking booking) {
        BookingForItemDto bookingForItemDto = new BookingForItemDto();
        bookingForItemDto.setId(booking.getId());
        bookingForItemDto.setStart(booking.getStart());
        bookingForItemDto.setEnd(booking.getEnd());
        bookingForItemDto.setItemId(booking.getItem().getId());
        bookingForItemDto.setBookerId(booking.getBooker().getId());
        return bookingForItemDto;
    }

    public Booking mapNewBookingToBooking(BookingNewDto bookingNewDto, Item item, User user) {
        Booking booking = new Booking();
        booking.setStart(bookingNewDto.getStart());
        booking.setEnd(bookingNewDto.getEnd());
        booking.setItem(item);
        booking.setBooker(user);
        return booking;
    }

    public BookingResponseDto mapBookingToResponseDto(Booking booking) {
        BookingResponseDto bookingResponseDto = new BookingResponseDto();
        bookingResponseDto.setId(booking.getId());
        bookingResponseDto.setStart(booking.getStart());
        bookingResponseDto.setEnd(booking.getEnd());
        bookingResponseDto.setItem(new BookingResponseDto.Item(booking.getItem().getId(), booking.getItem().getName()));
        bookingResponseDto.setBooker(new BookingResponseDto.Booker(booking.getBooker().getId(), booking.getBooker().getName()));
        bookingResponseDto.setStatus(booking.getStatus());
        return bookingResponseDto;
    }
}
