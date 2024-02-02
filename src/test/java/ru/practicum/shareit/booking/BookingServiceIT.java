package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingNewDto;
import ru.practicum.shareit.booking.enumerations.BookingStatus;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureTestDatabase
@SpringBootTest
@Transactional
@Rollback
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceIT {

    private final UserService userService;
    private final ItemService itemService;
    private final BookingServiceImpl bookingService;


    private Item item;
    private User owner;
    private User booker;
    private User user;
    private Booking booking;
    private BookingNewDto bookingNewDto;

    private LocalDateTime current;


    @BeforeEach
    void setUp() {
        current = LocalDateTime.now();
        owner = User.builder()
                .name("ownerName")
                .email("owner@mail.ru")
                .build();
        booker = User.builder()
                .name("bookerName")
                .email("booker@mail.ru")
                .build();
        user = User.builder()
                .name("userName")
                .email("user@mail.ru")
                .build();
        item = Item.builder()
                .name("itemName")
                .description("itemDescription")
                .available(true)
                .build();

        owner = userService.createUser(owner);
        booker = userService.createUser(booker);
        user = userService.createUser(user);
        item = itemService.createItem(owner.getId(), item);
        bookingNewDto = BookingNewDto.builder()
                .itemId(item.getId())
                .start(current.plusDays(1))
                .end(current.plusDays(4))
                .build();
        booking = bookingService.create(bookingNewDto, booker.getId());
    }

    @SneakyThrows
    @Test
    void getBookingById() {
        int bookingId = booking.getId();
        int userId = owner.getId();
        Booking booking1 = Booking.builder().build();
        Booking bookingGet = bookingService.getBookingById(bookingId, userId);
        BookingMapper.mapBookingToBookingForItem(bookingGet);
        MatcherAssert.assertThat(bookingGet, Matchers.equalTo(booking));
        assertNotEquals(booking, booking1);

    }

    @SneakyThrows
    @Test
    void getAllBookingByUser_StatusRejected() {
        booking.setStatus(BookingStatus.REJECTED);
        int bookerId = booker.getId();
        List<Booking> bookingGet = bookingService.getAllBookingByUser(bookerId, "REJECTED", 0, 10);
        MatcherAssert.assertThat(bookingGet, Matchers.contains(booking));
    }

    @SneakyThrows
    @Test
    void getAllBookingByUser_StatusAll() {
        int bookerId = booker.getId();
        List<Booking> bookingGet = bookingService.getAllBookingByUser(bookerId, "ALL", 0, 10);
        MatcherAssert.assertThat(bookingGet, Matchers.contains(booking));
    }

    @SneakyThrows
    @Test
    void getAllBookingByUser_StatusPast() {
        int bookerId = booker.getId();
        booking.setStart(current.minusDays(5));
        booking.setEnd(current.minusDays(2));
        List<Booking> bookingGet = bookingService.getAllBookingByUser(bookerId, "PAST", 0, 10);
        MatcherAssert.assertThat(bookingGet, Matchers.contains(booking));
    }

    @SneakyThrows
    @Test
    void findByItemOwner() {
        int ownerId = item.getOwner().getId();
        List<Booking> bookingsGet = bookingService.getAllBookingByOwner(ownerId, "ALL", 0, 10);
        MatcherAssert.assertThat(bookingsGet, Matchers.contains(booking));
    }

    @SneakyThrows
    @Test
    void setStatusApproved() {
        int ownerId = item.getOwner().getId();
        int bookingId = booking.getId();

        bookingService.approved(bookingId, ownerId, true);
        Booking bookingGet = bookingService.getBookingById(bookingId, ownerId);

        MatcherAssert.assertThat(bookingGet, Matchers.is(booking));
        MatcherAssert.assertThat(bookingGet.getStatus(), Matchers.is(BookingStatus.APPROVED));
    }

    @SneakyThrows
    @Test
    void findByUserId() {
        int userId = booker.getId();

        List<Booking> bookings = bookingService.getAllBookingByUser(userId, "WAITING", 0, 10);
        MatcherAssert.assertThat(bookings, Matchers.contains(booking));
    }

    @SneakyThrows
    @Test
    void findByWrongUserId() {
        int userId = user.getId();
        int bookingId = booking.getId();
        assertThrows(EntityNotFoundException.class, () -> bookingService.getBookingById(bookingId, userId));
    }
}
