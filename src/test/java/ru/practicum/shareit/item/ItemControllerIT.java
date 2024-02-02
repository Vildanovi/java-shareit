package ru.practicum.shareit.item;

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
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({ItemController.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemControllerIT {

    private final MockMvc mvc;
    private final ObjectMapper mapper;

    @MockBean
    private ItemService itemService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private ItemRepository itemRepository;

    @MockBean
    private BookingRepository bookingRepository;

    @MockBean
    private CommentRepository commentRepository;

    User owner;
    Item item;
    ItemDto itemDto;
    ItemResponseWithBookingDto itemResponseWithBookingDto;
    BookingForItemDto lastBookingForItemDto;
    BookingForItemDto nextBookingForItemDto;
    CommentForItemDto commentForItemDto;
    List<CommentForItemDto> listComments;
    List<ItemResponseWithBookingDto> listItemResponse;
    List<Item> items;

    @BeforeEach
    void setUp() {
        Instant current = Instant.now();
        owner = User.builder()
                .id(1)
                .name("ownerName")
                .email("owner@email.ru")
                .build();
        item = new Item(1,
                "itemName",
                "itemDescription",
                false,
                owner,
                1);
        itemDto = ItemDto.builder()
                .id(1)
                .name("itemName")
                .description("itemDescription")
                .available(false)
                .build();
        items = new ArrayList<>();
        items.add(item);
        lastBookingForItemDto = null;
        nextBookingForItemDto = null;
        commentForItemDto = CommentForItemDto.builder()
                .id(1)
                .text("commentText")
                .authorName("authorName")
                .created(current)
                .build();
        listComments = List.of(commentForItemDto);
        itemResponseWithBookingDto = ItemResponseWithBookingDto.builder()
                .id(1)
                .name("itemResponseName")
                .description("itemResponseDescription")
//                .available(true)
//                .lastBooking(null)
//                .nextBooking(null)
//                .comments(null)
                .build();
        listItemResponse = new ArrayList<>();
        listItemResponse.add(itemResponseWithBookingDto);
    }

    @SneakyThrows
    @Test
    void create_Item() {
        int ownerId = 1;
        when(itemService.createItem(anyInt(), any()))
                .thenReturn(item);

        mvc.perform(post("/items")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", ownerId)
                .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(item.getName())))
                .andExpect(jsonPath("$.description", is(item.getDescription())))
                .andExpect(jsonPath("$.available", is(item.getAvailable())));

        verify(itemService).createItem(anyInt(), any());
    }

    @SneakyThrows
    @Test
    void getItems() {
        int ownerId = 1;
        when(itemService.getItemsWithBookingByUserId(anyInt()))
                .thenReturn(listItemResponse);
        mvc.perform(get("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", ownerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", equalTo(itemResponseWithBookingDto.getId()), Integer.class))
                .andExpect(jsonPath("$[0].description", equalTo(itemResponseWithBookingDto.getDescription())));

        verify(itemService).getItemsWithBookingByUserId(ownerId);
    }

    @SneakyThrows
    @Test
    void search() {
        int ownerId = 1;
        String text = "item";
        when(itemService.searchByText(anyString()))
                .thenReturn(items);
        mvc.perform(get("/items/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .param("text", text)
                        .header("X-Sharer-User-Id", ownerId))
                .andExpect(status().isOk());

        verify(itemService).searchByText(text);
    }

    @SneakyThrows
    @Test
    void update() {
        int itemId = 1;
        int ownerId = 1;
        when(itemService.putItem(anyInt(), anyInt(), any()))
                .thenReturn(item);
        ItemResponseDto itemResponseDto = ItemMapper.itemResponseDto(item);

        String result = mvc.perform(patch("/items/{itemId}", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", ownerId)
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(result, mapper.writeValueAsString(itemResponseDto));
        verify(itemService).putItem(itemId, ownerId, item);
    }

    @SneakyThrows
    @Test
    void deleteItems() {
        int itemId = 1;
        when(itemService.deleteItem(anyInt()))
                .thenReturn(item);

        mvc.perform(delete("/items/{itemId}", itemId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk());
        verify(itemService).deleteItem(itemId);
    }

    @SneakyThrows
    @Test
    void getItemById() {
        int userId = owner.getId();
        int itemId = item.getId();

        when(itemService.getItemById(itemId, userId))
                .thenReturn(itemResponseWithBookingDto);

        mvc.perform(get("/items/{id}", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(item.getId()), Integer.class))
                .andExpect(jsonPath("$.name", equalTo(itemResponseWithBookingDto.getName())))
                .andExpect(jsonPath("$.description", equalTo(itemResponseWithBookingDto.getDescription())));

        verify(itemService).getItemById(itemId, userId);
    }

    @SneakyThrows
    @Test
    void addComment() {
        int userId = 1;
        int itemId = 1;
        Comment comment = Comment.builder()
                .id(1)
                .text("text")
                .item(item)
                .author(owner)
                .build();
        CommentDto commentDto = new CommentDto();
        commentDto.setText("comment");
        when(itemService.createComment(anyInt(), anyInt(), any()))
                .thenReturn(comment);

        String result = mvc.perform(post("/items/{itemId}/comment", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(comment.getId()), Integer.class))
                .andExpect(jsonPath("$.authorName", equalTo(comment.getAuthor().getName())))
                .andExpect(jsonPath("$.text", equalTo(comment.getText())))
                .andExpect(jsonPath("$.created", equalTo(comment.getCreated())))
                .andReturn()
                .getResponse()
                .getContentAsString();
        CommentResponseDto commentResponseDto = CommentMapper.mapCommentToResponseDto(comment);
        CommentMapper.mapCommentToCommentForItem(comment);
        assertEquals(result, mapper.writeValueAsString(commentResponseDto));
        verify(itemService).createComment(anyInt(), anyInt(), any());
    }
}
