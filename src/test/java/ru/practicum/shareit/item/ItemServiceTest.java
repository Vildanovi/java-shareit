package ru.practicum.shareit.item;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private ItemService itemService;

    private Item item;
    private Item item1;
    private User owner;
    private User booker;
    private Comment comment;

    @BeforeEach
    void setUp() {
        owner = User.builder()
                .id(1)
                .name("ownerName")
                .email("owner@mail.ru")
                .build();
        booker = User.builder()
                .name("bookerName")
                .email("booker@mail.ru")
                .build();
        item = Item.builder()
                .id(1)
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
        comment = Comment.builder()
                .text("itemComment")
                .build();
    }

    @Test
    void addComment() {
        int userId = 1;
        int itemId = 1;
        Comment comment1 = Comment.builder().build();
        Comment comment = Comment.builder()
                .text("itemComment")
                .build();
        User owner = User.builder()
                .id(1)
                .name("ownerName")
                .email("owner@mail.ru")
                .build();
        Item item = Item.builder()
                .id(1)
                .name("itemName")
                .description("itemDescription")
                .available(true)
                .owner(owner)
                .build();
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(owner));
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(item));
        when(bookingRepository
                .existsByBooker_IdAndItem_IdAndEndIsBeforeOrderByStartDesc(anyInt(), anyInt(), ArgumentMatchers.any()))
                .thenReturn(true);
        comment.setId(1);
        comment.setItem(item);
        comment.setAuthor(owner);
        comment.setCreated(Instant.now());
        when(commentRepository.save(any()))
                .thenReturn(comment);

        Comment createdComment = itemService.createComment(itemId, userId, comment);
        assertEquals(createdComment, comment);
        assertNotEquals(comment1, comment);
        MatcherAssert.assertThat(createdComment.getId(), equalTo(1));
    }

    @Test
    void getById_NotFoundItemException() {
        int itemId = 1;
        int userId = 100;

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(new User()));

        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class, () -> itemService.getItemById(itemId, userId));
        assertEquals(entityNotFoundException.getMessage(), "Объект не найден: 1");
        verify(userRepository).findById(userId);
        verify(itemRepository).findById(itemId);
    }

    @Test
    void getById_NotFoundUserException() {
        int itemId = 100;
        int userId = 1;

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(new User()));

        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class, () -> itemService.getItemById(itemId, userId));
        assertEquals(entityNotFoundException.getMessage(), "Объект не найден: 100");
        verify(userRepository).findById(userId);
        verify(itemRepository).findById(itemId);
    }

    @Test
    void search() {
        String query = " ";
        List<Item> itemsGet = itemService.searchByText(query);
        assertThat(itemsGet, hasSize(0));
    }

    @Test
    void addComment_ItemEntityNotFound() {
        int userId = owner.getId();
        int itemId = item.getId();

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(owner));
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemService.createComment(itemId, userId, comment));
        verify(commentRepository, never()).save(comment);
    }

    @Test
    void addComment_UserEntityNotFoundThrown() {
        int userId = owner.getId();
        int itemId = item.getId();

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemService.createComment(itemId, userId, comment));
        verify(commentRepository, never()).save(comment);
    }

    @Test
    void update_ItemNotExists() {
        int userId = 1;
        int itemId = 1;

        Item updateItem = new Item();
        updateItem.setName("Updated name");
        updateItem.setDescription("Updated description");
        updateItem.setAvailable(false);

        when(itemRepository.findById(itemId))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemService.putItem(itemId, userId, updateItem));
        verify(itemRepository).findById(itemId);
        verify(itemRepository, never()).save(item);
    }

    @Test
    void deleteItem() {
        int itemId = item.getId();
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemService.deleteItem(itemId));
        verify(itemRepository).findById(itemId);
    }
}
