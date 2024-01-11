//package ru.practicum.shareit.booking.service;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import ru.practicum.shareit.booking.dto.BookingDto;
//import ru.practicum.shareit.booking.dto.BookingResponseDto;
//import ru.practicum.shareit.booking.mapper.BookingMapper;
//import ru.practicum.shareit.booking.model.Booking;
//import ru.practicum.shareit.booking.repository.BookingRepository;
//import ru.practicum.shareit.exception.EntityNotFoundException;
//
//@Service
//@RequiredArgsConstructor
//public class BookingServiceImpl implements BookingService {
//
//    private final BookingRepository bookingRepository;
//
//    @Override
//    public Booking getBookingById(int id) {
//        return bookingRepository.findById(id)
//                .orElseThrow(() -> new EntityNotFoundException("Объект не найден: " + id));
//    }
//
//    @Override
//    public BookingResponseDto saveBooking(BookingDto bookingDto, int userId) {
//        return BookingMapper
//                .mapToBookingResponseDto(bookingRepository.save(BookingMapper.mapToNewBooking(bookingDto)));
//    }
//}
