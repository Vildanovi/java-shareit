package ru.practicum.shareit.booking.mapper;

import lombok.experimental.UtilityClass;
import org.mapstruct.control.MappingControl;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingNewDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.enumerations.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class BookingMapper {

    public static Booking mapBookingDtoToBooking(BookingDto bookingDto, Item item) {
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        return booking;
    }

    public static Booking mapNewBookingToBooking(BookingNewDto bookingNewDto, Item item, User user) {
        Booking booking = new Booking();
        booking.setStart(bookingNewDto.getStart());
        booking.setEnd(bookingNewDto.getEnd());
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.WAITING);
        return booking;
    }

    public static BookingResponseDto mapBookingToResponseDto(Booking booking) {
        BookingResponseDto bookingResponseDto = new BookingResponseDto();
        bookingResponseDto.setId(booking.getId());
        bookingResponseDto.setStart(booking.getStart());
        bookingResponseDto.setEnd(booking.getEnd());
        bookingResponseDto.setItem(booking.getItem());
        bookingResponseDto.setBooker(booking.getBooker());
        bookingResponseDto.setStatus(booking.getStatus());
        return bookingResponseDto;
    }
}
