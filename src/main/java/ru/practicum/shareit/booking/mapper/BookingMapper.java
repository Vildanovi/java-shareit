//package ru.practicum.shareit.booking.mapper;
//
//import ru.practicum.shareit.booking.dto.BookingDto;
//import ru.practicum.shareit.booking.dto.BookingResponseDto;
//import ru.practicum.shareit.booking.enumerations.BookingStatus;
//import ru.practicum.shareit.booking.model.Booking;
//import ru.practicum.shareit.item.model.Item;
//
//public class BookingMapper {
//
//    public static Booking BookingDtoToBooking(BookingDto bookingDto, Item item) {
//        Booking booking = new Booking();
//        booking.setItem(item);
//        booking.setStart(bookingDto.getStart());
//        booking.setEnd(bookingDto.getEnd());
//        return booking;
//    }
//
//    public static Booking mapToNewBooking(BookingDto bookingDto) {
//        Booking booking = new Booking();
//        booking.setStart(bookingDto.getStart());
//        booking.setEnd(bookingDto.getEnd());
//        booking.setStatus(BookingStatus.WAITING);
//        return booking;
//    }
//
//    public static BookingResponseDto mapToBookingResponseDto(Booking booking) {
//        BookingResponseDto bookingResponseDto = new BookingResponseDto();
//        bookingResponseDto.setId(booking.getId());
//        bookingResponseDto.setStart(booking.getStart());
//        bookingResponseDto.setEnd(booking.getEnd());
//        bookingResponseDto.setItem(booking.getItem());
//        bookingResponseDto.setBooker(booking.getBooker());
//        bookingResponseDto.setStatus(booking.getStatus());
//        return bookingResponseDto;
//    }
//}
