package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") int userId,
                                         @RequestBody @Valid BookItemRequestDto requestDto) {
        log.info("Creating booking {}, userId={}", requestDto, userId);
        return bookingClient.create(userId, requestDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@RequestHeader("X-Sharer-User-Id") int userId,
                                          @PathVariable @Positive int bookingId) {
        log.info("Get booking with id: {}, userId={}", bookingId, userId);
        return bookingClient.getBookingById(userId, bookingId);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllByOwner(@RequestHeader("X-Sharer-User-Id") int userId,
                                                @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                                @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState state = throwableIfStateInvalid(stateParam);
        log.info("Get bookings by owner with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getAllBookingByOwner(userId, state, from, size);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByUser(@RequestParam(value = "state", defaultValue = "ALL") String state,
                                               @RequestHeader("X-Sharer-User-Id") int userId,
                                               @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero int from,
                                               @RequestParam(value = "size", defaultValue = "10") @Positive int size) {
        BookingState stateParam = throwableIfStateInvalid(state);
        log.info("Get bookings by booker with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getAllBookingByUser(userId, stateParam, from, size);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> bookingApproved(@PathVariable @Positive int bookingId,
                                                  @RequestParam boolean approved,
                                                  @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("Approve booking with id: {}, approved = {}, userId = {}", bookingId, approved, userId);
        return bookingClient.approved(bookingId, approved, userId);
    }

    private BookingState throwableIfStateInvalid(String stateParam) {
        return BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: UNSUPPORTED_STATUS"));
    }
}