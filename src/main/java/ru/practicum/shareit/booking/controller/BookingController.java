package ru.practicum.shareit.booking.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingNewDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.constant.Constants;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @Operation(summary = "Забронировать вещь")
    public BookingResponseDto create(@RequestHeader(Constants.USER_ID) int userId,
                                     @Validated(CreatedBy.class) @RequestBody BookingNewDto bookingNewDto) {
        return BookingMapper.mapBookingToResponseDto(bookingService.create(bookingNewDto, userId));
    }

    @GetMapping("/{bookingId}")
    @Operation(summary = "Получить бронирование по идентификатору")
    public BookingResponseDto getBookingById(@RequestHeader(Constants.USER_ID) int userId,
                                             @PathVariable(value = "bookingId") int bookingId) {
        return BookingMapper.mapBookingToResponseDto(bookingService.getBookingById(bookingId, userId));
    }

    @GetMapping("/owner")
    @Operation(summary = "Получить все бронирования владельца")
    public List<BookingResponseDto> getAllByOwner(@RequestHeader(Constants.USER_ID) int userId) {
        return bookingService.getAllBookingByOwner(userId).stream()
                .map(BookingMapper::mapBookingToResponseDto)
                .collect(Collectors.toList());
        //Добавить State
        //@RequestParam(value = "state", defaultValue = "ALL") String state
    }

    @GetMapping()
    @Operation(summary = "Получить все бронирования пользователя")
    public List<BookingResponseDto> getAllByUser(@RequestParam(value = "state", defaultValue = "ALL") String state,
                                                 @RequestHeader(Constants.USER_ID) int userId) {
        return bookingService.getAllBookingByUser(userId, state).stream()
                .map(BookingMapper::mapBookingToResponseDto)
                .collect(Collectors.toList());
    }


    @PatchMapping("/{bookingId}")
    @Operation(summary = "Подтверждение бронирования")
    public BookingResponseDto bookingApproved(@PathVariable(value = "bookingId") int bookingId,
                                              @RequestHeader(Constants.USER_ID) int ownerId,
                                              @RequestParam(value = "approved") boolean approved) {
        return BookingMapper.mapBookingToResponseDto(bookingService.approved(bookingId, ownerId, approved));
    }
}
