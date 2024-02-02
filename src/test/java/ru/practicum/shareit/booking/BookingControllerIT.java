package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingNewDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.enumerations.BookingStatus;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidationBadRequestException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingControllerIT {

    private final MockMvc mvc;
    private final ObjectMapper mapper;

    @MockBean
    private BookingService bookingService;

    LocalDateTime currentDate;
    LocalDateTime start;
    LocalDateTime end;
    Item item;
    User booker;
    User owner;
    BookingNewDto bookingNewDto;
    Booking booking;

    @BeforeEach
    void setUp() {
        currentDate = LocalDateTime.now();
        start = currentDate.plusDays(1);
        end = currentDate.plusDays(3);
        booker = User.builder()
                .id(1)
                .name("bookerName")
                .email("booker@mail.ru")
                .build();
        owner = User.builder()
                .id(2)
                .name("ownerName")
                .email("owner@mail.ru")
                .build();
        item = Item.builder()
                .id(1)
                .name("itemName")
                .description("itemDescription")
                .available(true)
                .owner(owner)
                .build();
        bookingNewDto = BookingNewDto.builder()
                .itemId(1)
                .start(currentDate.plusDays(1))
                .end(currentDate.plusDays(3))
                .build();
        booking = Booking.builder()
                .id(1)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();
    }

    @SneakyThrows
    @Test
    void create_Booking() {
        int bookerId = 1;
        when(bookingService.create(any(), anyInt()))
                .thenReturn(booking);
        mvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", bookerId)
                        .content(mapper.writeValueAsString(bookingNewDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId()), Integer.class))
                .andExpect(jsonPath("$.item.id", is(item.getId()), Integer.class))
                .andExpect(jsonPath("$.start").hasJsonPath())
                .andExpect(jsonPath("$.end").hasJsonPath())
                .andExpect(jsonPath("$.status").hasJsonPath());

        verify(bookingService).create(any(), anyInt());
    }

    @SneakyThrows
    @Test
    void getById() {
        int bookerId = 1;
        int bookingId = 1;

        when(bookingService.getBookingById(bookingId, bookerId))
                .thenThrow(EntityNotFoundException.class);

        mvc.perform(get("/bookings/{bookingId}", bookingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", bookerId))
                .andExpect(status().isNotFound());

        verify(bookingService).getBookingById(bookingId, bookerId);
    }

    @SneakyThrows
    @Test
    void getAllByOwner_badState() {
        int bookerId = 1;
        String badState = "UNSUPPORTED_STATUS";
        when(bookingService.getAllBookingByOwner(anyInt(), any(), anyInt(), anyInt()))
                .thenThrow(ValidationBadRequestException.class);

        mvc.perform(get("/bookings/owner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", bookerId)
                        .param("state", badState)
                        .param("from", "0")
                        .param("size", "10")
                )
                .andExpect(status().isBadRequest());

        verify(bookingService).getAllBookingByOwner(anyInt(), any(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void getAllByUser() {
        int bookerId = 1;
        String badState = "UNSUPPORTED_STATUS";
        when(bookingService.getAllBookingByUser(anyInt(), any(), anyInt(), anyInt()))
                .thenThrow(ValidationBadRequestException.class);

        mvc.perform(get("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", bookerId)
                        .param("state", badState)
                        .param("from", "0")
                        .param("size", "10")
                )
                .andExpect(status().isBadRequest());

        verify(bookingService).getAllBookingByUser(anyInt(), any(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void approve() {
        int ownerId = 1;
        int bookingId = 1;
        when(bookingService.approved(anyInt(), anyInt(), anyBoolean()))
                .thenReturn(booking);
        BookingResponseDto bookingResponseDto = BookingMapper.mapBookingToResponseDto(booking);
        String result = mvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", ownerId)
                        .param("approved", Boolean.toString(true)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(result, mapper.writeValueAsString(bookingResponseDto));
        verify(bookingService).approved(bookingId, ownerId, true);
    }
}
