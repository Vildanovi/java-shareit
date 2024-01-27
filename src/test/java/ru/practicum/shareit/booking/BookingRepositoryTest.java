package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.enumerations.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingRepositoryTest {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    private User owner;
    private User booker;
    private User booker2;
    private Item itemByOwner;
    private Item item2ByOwner;
    private Booking bookingByBookerAndApproved;
    private Booking bookingByBookerAndCanceled;
    private Booking bookingByBookerAndStartBeforeWaiting;
    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd:HH-mm");
    private final LocalDateTime currentDate = LocalDateTime.parse(LocalDateTime.now().format(dateFormat), dateFormat);


    @BeforeEach
    void setUp() {
        owner = userRepository.save(User.builder()
                .name("ownerName")
                .email("owner@mail.ru")
                .build());

        booker = userRepository.save(User.builder()
                .name("bookerName")
                .email("booker@mail.ru")
                .build());

        booker2 = userRepository.save(User.builder()
                .name("booker2Name")
                .email("booker2@mail.ru")
                .build());

        itemByOwner = itemRepository.save(Item.builder()
                .name("itemName")
                .description("itemDescription")
                .available(true)
                .owner(owner)
                .build());

        item2ByOwner = itemRepository.save(Item.builder()
                .name("itemName")
                .description("itemDescription")
                .available(true)
                .owner(owner)
                .build());

        bookingByBookerAndApproved = bookingRepository.save(Booking.builder()
                .start(currentDate.plusDays(1))
                .end(currentDate.plusDays(3))
                .item(itemByOwner)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build());

        bookingByBookerAndCanceled = bookingRepository.save(Booking.builder()
                .start(currentDate.plusDays(2))
                .end(currentDate.plusDays(4))
                .item(item2ByOwner)
                .booker(booker)
                .status(BookingStatus.CANCELED)
                .build());

        bookingByBookerAndStartBeforeWaiting = bookingRepository.save(Booking.builder()
                .start(currentDate.minusDays(2))
                .end(currentDate.plusDays(4))
                .item(item2ByOwner)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build());
    }

    @AfterEach
    void cleanRepositories() {
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void findBookingByItemOwner() {
        Pageable page = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "start"));
        List<Booking> bookingResult = bookingRepository.findAllByItem_Owner_Id(owner.getId(), page);
        assertThat(bookingResult, hasSize(1));
    }

    @Test
    void findBookingByStatus() {
        Pageable page = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "start"));
        List<Booking> bookingResult = bookingRepository.findAllByItem_Owner_IdAndStatus(owner.getId(), BookingStatus.CANCELED, page);
        assertThat(bookingResult, hasSize(1));
    }

    @Test
    void findBookingByStartDateAfterCurrent() {
        Pageable page = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "start"));
        List<Booking> bookingResult = bookingRepository.findAllByBooker_IdAndStartAfter(booker.getId(), currentDate, page);
        assertThat(bookingResult, hasSize(2));
    }

    @Test
    void findBookingItem2ByOwnerStartBeforeCurrent() {
        Pageable page = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "start"));
        List<Booking> bookingResult = bookingRepository
                .findAllByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(item2ByOwner
                        .getOwner().getId(), currentDate, currentDate, page);
        assertThat(bookingResult, hasSize(1));
    }

}
