package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingNewDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
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

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public Booking getBookingById(int id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Объект не найден: " + id));
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
        if(item.getOwner() == userId) {
            throw new EntityNotFoundException("Объект не может забронировать владелец: " + userId);
        }
        Booking booking = BookingMapper.mapNewBookingToBooking(bookingNewDto, item, user);
        bookingRepository.save(booking);
        return booking;
    }
}
