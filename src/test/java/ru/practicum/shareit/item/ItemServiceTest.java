package ru.practicum.shareit.item;


import lombok.RequiredArgsConstructor;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingNewDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dto.ItemResponseWithBookingDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@AutoConfigureTestDatabase
@SpringBootTest
@Transactional
@Rollback
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceTest {

    private final UserService userService;
    private final ItemService itemService;
    private final BookingServiceImpl bookingService;

    private Item item;
    private Item item1;
    private User owner;
    private User booker;
    private Booking booking;
    private BookingNewDto bookingNewDto;
    private Comment comment;
    private LocalDateTime current;

    @BeforeEach
    void setUp() {
        current = LocalDateTime.now();
        item = Item.builder()
                .name("itemName")
                .description("itemDescription")
                .available(true)
                .build();
        item1 = Item.builder()
                .name("search")
                .description("item1Description")
                .available(true)
                .build();
        owner = User.builder()
                .name("ownerName")
                .email("owner@mail.ru")
                .build();
        booker = User.builder()
                .name("bookerName")
                .email("booker@mail.ru")
                .build();
        owner = userService.createUser(owner);
        booker = userService.createUser(booker);
        item = itemService.createItem(owner.getId(), item);
        item1 = itemService.createItem(booker.getId(), item1);
        bookingNewDto = BookingNewDto.builder()
                .itemId(item.getId())
                .start(current)
                .end(current.plusSeconds(1))
                .build();
        booking = bookingService.create(bookingNewDto, booker.getId());
    }

    @Test
    void getById() {
        int bookingId = booking.getId();
        int itemId = item.getId();
        int ownerId = owner.getId();
        bookingService.approved(bookingId, ownerId, true);

        ItemResponseWithBookingDto itemGet = itemService.getItemById(itemId, ownerId);

        MatcherAssert.assertThat(itemGet.getName(), equalTo(item.getName()));
    }

    @Test
    void getAllById() {
        int ownerId = owner.getId();
        List<ItemResponseWithBookingDto> itemsGet = itemService.getItemsWithBookingByUserId(ownerId);
        assertThat(itemsGet, hasSize(1));
    }

    @Test
    void search() {
        String query = "search";
        List<Item> itemsGet = itemService.searchByText(query);
        assertThat(itemsGet, hasSize(1));
    }

    @Test
    void put() {
        int itemId = item.getId();
        int userId = owner.getId();
        Item item1 = Item.builder()
                .name("updateName")
                .description("updateDescription")
                .available(false)
                .build();
        Item itemGet = itemService.putItem(itemId, userId, item1);
        MatcherAssert.assertThat(itemGet.getName(), equalTo(item1.getName()));
        MatcherAssert.assertThat(itemGet.getDescription(), equalTo(item1.getDescription()));
        MatcherAssert.assertThat(itemGet.getAvailable(), equalTo(item1.getAvailable()));
    }

    @Test
    void delete() {
        int itemId = item.getId();
        int ownerId = owner.getId();

        itemService.deleteItem(itemId);
        assertThrows(EntityNotFoundException.class, () -> itemService.getItemById(itemId, ownerId));
    }
}
