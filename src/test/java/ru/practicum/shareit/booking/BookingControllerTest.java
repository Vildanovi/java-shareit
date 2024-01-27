package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingNewDto;
import ru.practicum.shareit.booking.enumerations.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingControllerTest {

    private final MockMvc mvc;
    private final ObjectMapper mapper;

    @MockBean
    private BookingService bookingService;

    @SneakyThrows
    @Test
    void create_Booking() {
        LocalDateTime currentDate = LocalDateTime.now();
        LocalDateTime start = currentDate.plusDays(1);
        LocalDateTime end = currentDate.plusDays(3);
        Item item = new Item();
        User booker = new User();
        item.setId(1);
        booker.setId(1);
        int userId = 1;
        BookingNewDto bookingNewDto = BookingNewDto.builder()
                .itemId(1)
                .start(currentDate.plusDays(1))
                .end(currentDate.plusDays(3))
                .build();

        Booking booking = new Booking(1, start, end, item, booker, BookingStatus.WAITING);
        when(bookingService.create(any(), anyInt()))
                .thenReturn(booking);

        mvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(bookingNewDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1), Integer.class))
                .andExpect(jsonPath("$.item.id", is(booking.getId()), Integer.class))
                .andExpect(jsonPath("$.start").hasJsonPath())
                .andExpect(jsonPath("$.end").hasJsonPath())
                .andExpect(jsonPath("$.status").hasJsonPath());

        verify(bookingService).create(any(), anyInt());
    }
}
