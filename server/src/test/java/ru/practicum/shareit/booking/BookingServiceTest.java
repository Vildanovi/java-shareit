package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dto.BookingNewDto;
import ru.practicum.shareit.booking.enumerations.BookingState;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private Item item;
    private User owner;
    private User booker;
    private Booking booking;
    private Booking bookingRejected;
    private BookingNewDto bookingNewDto;
    LocalDateTime currentDate;
    LocalDateTime start;
    LocalDateTime end;

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
                .build();
        bookingRejected = Booking.builder()
                .id(2)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .build();
    }

    @Test
    void findByOwnerId_Rejected() {
        int ownerId = 2;
        List<Booking> bookings = new ArrayList<>();
        bookings.add(bookingRejected);
        when(userRepository.findById(ownerId))
                .thenReturn(Optional.of(owner));
        when(bookingRepository.findAllByItem_Owner_IdAndStatus(anyInt(), any(), any()))
                .thenReturn(bookings);

        List<Booking> bookingsRejected = bookingService.getAllBookingByOwner(ownerId, "REJECTED", 0, 10);

        assertThat(bookingsRejected, contains(bookingRejected));
        verify(userRepository).findById(ownerId);
        verify(bookingRepository).findAllByItem_Owner_IdAndStatus(anyInt(), any(), any());
    }

    @Test
    void findByOwnerId_WAITING() {
        int ownerId = 2;
        List<Booking> bookings = new ArrayList<>();
        bookings.add(bookingRejected);
        when(userRepository.findById(ownerId))
                .thenReturn(Optional.of(owner));
        when(bookingRepository.findAllByItem_Owner_IdAndStatus(anyInt(), any(), any()))
                .thenReturn(bookings);

        List<Booking> bookingsRejected = bookingService.getAllBookingByOwner(ownerId, "WAITING", 0, 10);

        assertThat(bookingsRejected, contains(bookingRejected));
        verify(userRepository).findById(ownerId);
        verify(bookingRepository).findAllByItem_Owner_IdAndStatus(anyInt(), any(), any());
    }

    @ParameterizedTest
    @EnumSource(value = BookingState.class, names = {"REJECTED", "WAITING"})
    void findByItemOwner_Status(BookingState bookingState) {
        int userId = 1;
        int from = 0;
        int size = 10;
        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByItem_Owner_IdAndStatus(
                anyInt(), any(), any()))
                .thenReturn(bookings);

        List<Booking> actualBookings = bookingService.getAllBookingByOwner(userId, bookingState.toString(), from, size);

        assertThat(actualBookings, contains(booking));
        verify(userRepository).findById(userId);
        verify(bookingRepository).findAllByItem_Owner_IdAndStatus(
                anyInt(), any(), any());
    }

    @Test
    void findByUserId_Future() {
        int ownerId = 2;
        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking);
        when(userRepository.findById(ownerId))
                .thenReturn(Optional.of(owner));
        when(bookingRepository.findAllByBooker_IdAndStartAfter(anyInt(), any(), any()))
                .thenReturn(bookings);

        List<Booking> bookingsStatus = bookingService.getAllBookingByUser(ownerId, "FUTURE", 0, 10);

        assertThat(bookingsStatus, hasSize(1));
        verify(userRepository).findById(ownerId);
        verify(bookingRepository).findAllByBooker_IdAndStartAfter(anyInt(), any(), any());
    }

    @Test
    void findByOwnerId_Past() {
        int ownerId = 2;
        List<Booking> bookings = new ArrayList<>();
        booking.setStart(currentDate.minusDays(1));
        bookings.add(booking);
        when(userRepository.findById(ownerId))
                .thenReturn(Optional.of(owner));
        when(bookingRepository.findAllByItem_Owner_IdAndEndIsBefore(anyInt(), any(), any()))
                .thenReturn(bookings);

        List<Booking> bookingsStatus = bookingService.getAllBookingByOwner(ownerId, "PAST", 0, 10);

        assertThat(bookingsStatus, contains(booking));
        verify(userRepository).findById(ownerId);
        verify(bookingRepository).findAllByItem_Owner_IdAndEndIsBefore(anyInt(), any(), any());
    }

    @Test
    void findByUserId_Past() {
        int bookerId = 1;
        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking);
        when(userRepository.findById(bookerId))
                .thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBooker_IdAndEndIsBefore(anyInt(), any(), any()))
                .thenReturn(bookings);

        List<Booking> bookingsStatus = bookingService.getAllBookingByUser(bookerId, "PAST", 0, 10);

        assertThat(bookingsStatus, hasSize(1));
        verify(userRepository).findById(bookerId);
        verify(bookingRepository).findAllByBooker_IdAndEndIsBefore(anyInt(), any(), any());
    }

    @Test
    void findByOwnerId_Current() {
        int ownerId = 2;
        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking);
        when(userRepository.findById(ownerId))
                .thenReturn(Optional.of(owner));
        when(bookingRepository.findAllByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(anyInt(), any(), any(), any()))
                .thenReturn(bookings);

        List<Booking> bookingsStatus = bookingService.getAllBookingByOwner(ownerId, "CURRENT", 0, 10);

        assertThat(bookingsStatus, contains(booking));
        verify(userRepository).findById(ownerId);
        verify(bookingRepository).findAllByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(anyInt(), any(), any(), any());
    }

    @Test
    void findByUserId_Current() {
        int ownerId = 2;
        List<Booking> bookings = new ArrayList<>();
        booking.setStart(currentDate.minusDays(5));
        bookings.add(booking);
        when(userRepository.findById(ownerId))
                .thenReturn(Optional.of(owner));
        when(bookingRepository.findAllByBooker_IdAndStartIsBeforeAndEndIsAfter(anyInt(), any(), any(), any()))
                .thenReturn(bookings);

        List<Booking> bookingsStatus = bookingService.getAllBookingByUser(ownerId, "CURRENT", 0, 10);

        assertThat(bookingsStatus, hasSize(1));
        verify(userRepository).findById(ownerId);
        verify(bookingRepository).findAllByBooker_IdAndStartIsBeforeAndEndIsAfter(anyInt(), any(), any(), any());
    }

    @Test
    void findByOwnerId_Future() {
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        Pageable page = PageRequest.of(0 / 10, 10, sort);
        int ownerId = 2;
        LocalDateTime startFuture = currentDate.plusDays(1);
        LocalDateTime endFuture = currentDate.plusDays(3);
        booking.setStart(startFuture);
        booking.setEnd(endFuture);
        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking);
        when(userRepository.findById(ownerId))
                .thenReturn(Optional.of(owner));
        when(bookingRepository.findAllByItem_Owner_IdAndStartAfter(anyInt(), any(), any()))
                .thenReturn(bookings);

        List<Booking> bookingsStatus = bookingService.getAllBookingByOwner(ownerId, "FUTURE", 0, 10);

        assertThat(bookingsStatus, contains(booking));
        verify(userRepository).findById(ownerId);
    }
}
