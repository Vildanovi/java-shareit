package ru.practicum.shareit.item;


import lombok.RequiredArgsConstructor;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingNewDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidationBadRequestException;
import ru.practicum.shareit.item.dto.ItemResponseWithBookingDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@AutoConfigureTestDatabase
@SpringBootTest
@Transactional
@Rollback
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceIT {

    private final UserService userService;
    private final ItemService itemService;
    private final BookingService bookingService;

    @MockBean
    private final BookingRepository bookingRepository;

    @MockBean
    private final CommentRepository commentRepository;

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

        owner = User.builder()
                .name("ownerName")
                .email("owner@mail.ru")
                .build();
        booker = User.builder()
                .name("bookerName")
                .email("booker@mail.ru")
                .build();
        owner = userService.createUser(owner);
        item = Item.builder()
                .name("itemName")
                .description("itemDescription")
                .available(true)
                .owner(owner)
                .build();
        item1 = Item.builder()
                .name("search")
                .description("item1Description")
                .available(true)
                .build();
        booker = userService.createUser(booker);
        item = itemService.createItem(owner.getId(), item);
        item1 = itemService.createItem(booker.getId(), item1);
        bookingNewDto = BookingNewDto.builder()
                .itemId(item.getId())
                .start(current)
                .end(current.plusDays(1))
                .build();
        booking = bookingService.create(bookingNewDto, booker.getId());
        booking.setStart(current.minusDays(10));
        booking.setEnd(current.minusDays(5));
        booking.setId(1);
        comment = Comment.builder()
                .text("itemComment")
                .build();
    }

    @Test
    void getById() {
        int itemId = item.getId();
        int ownerId = owner.getId();

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
    void getItemsWithBookingByUserId() {
        int ownerId = owner.getId();
        List<Booking> bookingList = new ArrayList<>();
        bookingList.add(booking);
        List<Comment> commentList = new ArrayList<>();
        comment.setId(1);
        comment.setAuthor(owner);
        comment.setCreated(Instant.now());
        commentList.add(comment);
        when(bookingRepository
                .findAllByItem_IdIn(anyList(), ArgumentMatchers.any()))
                .thenReturn(bookingList);
        when(commentRepository.findByItem_IdIn(ArgumentMatchers.any()))
                .thenReturn(commentList);

        List<ItemResponseWithBookingDto> itemsGet = itemService.getItemsWithBookingByUserId(ownerId);

        assertThat(itemsGet, hasSize(1));
    }

    @Test
    void getItemById() {
        int itemId = item.getId();
        int ownerId = owner.getId();
        Booking lastBooking = Booking.builder()
                .id(1)
                .start(current.minusDays(5))
                .end(current)
                .item(item)
                .booker(booker)
                .build();
        Booking nextBooking = Booking.builder()
                .id(2)
                .start(current.plusDays(5))
                .end(current.plusDays(6))
                .item(item)
                .booker(booker)
                .build();
        List<Comment> commentList = new ArrayList<>();
        comment.setId(1);
        comment.setAuthor(owner);
        comment.setCreated(Instant.now());
        commentList.add(comment);
        when(bookingRepository
                .findFirstByItem_IdAndStatusAndStartLessThanEqualOrderByEndDesc(anyInt(), ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(lastBooking);
        when(bookingRepository
                .findFirstByItem_IdAndStatusAndStartIsAfterOrderByStartAsc(anyInt(), ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(nextBooking);
        when(commentRepository.findAllByItem_Id(anyInt()))
                .thenReturn(commentList);
        ItemResponseWithBookingDto foundItem = itemService.getItemById(itemId, ownerId);

        assertThat(foundItem.getName(), equalTo(item.getName()));
    }

    @Test
    void search() {
        String query = "search";
        List<Item> itemsGet = itemService.searchByText(query);
        assertThat(itemsGet, hasSize(1));
    }

    @Test
    void search_empty() {
        String query = "JHF";
        List<Item> itemsGet = itemService.searchByText(query);
        assertThat(itemsGet, hasSize(0));
    }

    @Test
    void put() {
        int itemId = item.getId();
        int userId = owner.getId();

        Item itemGet = itemService.putItem(itemId, userId, item1);
        ItemMapper.toItemDto(itemGet);

        MatcherAssert.assertThat(itemGet.getName(), equalTo(item1.getName()));
        MatcherAssert.assertThat(itemGet.getDescription(), equalTo(item1.getDescription()));
        MatcherAssert.assertThat(itemGet.getAvailable(), equalTo(item1.getAvailable()));
    }

    @Test
    void put_Exception() {
        int itemId = item.getId();
        int userId = booker.getId();
        assertThrows(EntityNotFoundException.class, () -> itemService.putItem(itemId, userId, item1));
    }

    @Test
    void searchAvailable() {
        String search = "name";

        List<Item> foundItems = itemService.searchByText(search);

        MatcherAssert.assertThat(foundItems, contains(item));
        assertThat(foundItems.get(0).hashCode(), equalTo(item.hashCode()));
        assertThat(foundItems.get(0), equalTo(item));
    }

    @Test
    void delete() {
        int itemId = item.getId();
        int ownerId = owner.getId();

        itemService.deleteItem(itemId);
        assertThrows(EntityNotFoundException.class, () -> itemService.getItemById(itemId, ownerId));
    }

    @Test
    void addComment_Exception() {
        int userId = booker.getId();
        int itemId = item.getId();
        when(bookingRepository
                .existsByBooker_IdAndItem_IdAndEndIsBeforeOrderByStartDesc(userId, itemId, LocalDateTime.now()))
                .thenReturn(false);
        ValidationBadRequestException validationBadRequestException = assertThrows(ValidationBadRequestException.class, () -> itemService.createComment(itemId, userId, comment));
        MatcherAssert.assertThat(validationBadRequestException.getMessage(), equalTo("Нет завершенных бронирований"));
    }
}
