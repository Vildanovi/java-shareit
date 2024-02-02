package ru.practicum.shareit.item;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.Instant;
import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
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
}
