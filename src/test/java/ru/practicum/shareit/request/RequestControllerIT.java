package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestNewDto;
import ru.practicum.shareit.request.dto.ItemResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.RequestServiceImpl;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertEquals;

@WebMvcTest({ItemRequestController.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RequestControllerIT {

    private final MockMvc mvc;
    private final ObjectMapper mapper;

    @MockBean
    private RequestServiceImpl requestService;

    ItemResponseDto itemResponseDto;
    LocalDateTime current;
    List<ItemResponseDto> itemsResponse;

    @BeforeEach
    void setUp() {
        current = LocalDateTime.now();
        itemResponseDto = ItemResponseDto.builder()
                .id(1)
                .description("itemDescription")
                .requestor(1)
                .created(current)
                .build();
        itemsResponse = new ArrayList<>();
        itemsResponse.add(itemResponseDto);
    }

//    @BeforeEach
//    void setUp() {
//        Instant current = Instant.now();
//        owner = new User();
//        owner.setId(1);
//        item = new Item(1,
//                "itemName",
//                "itemDescription",
//                false, owner,
//                1);
//        itemDto = ItemDto.builder()
//                .id(1)
//                .name("itemName")
//                .description("itemDescription")
//                .available(false)
//                .build();
//        lastBookingForItemDto = null;
//        nextBookingForItemDto = null;
//        commentForItemDto = CommentForItemDto.builder()
//                .id(1)
//                .text("commentText")
//                .authorName("authorName")
//                .created(current)
//                .build();
//        listComments = List.of(commentForItemDto);
//        itemResponseWithBookingDto = ItemResponseWithBookingDto.builder()
//                .id(1)
//                .name("itemResponseName")
//                .description("itemResponseDescription")
////                .available(true)
////                .lastBooking(null)
////                .nextBooking(null)
////                .comments(null)
//                .build();
//        listItemResponse = new ArrayList<>();
////        listItemResponse.add(itemResponseWithBookingDto);
//    }

    @SneakyThrows
    @Test
    void create_Request() {
        int requestId = 1;
        ItemRequestNewDto itemRequestNewDto = ItemRequestNewDto.builder()
                .description("requestDescription")
                .build();
        ItemRequest itemRequest = ItemRequest.builder()
                .id(requestId)
                .description("requestDescription")
                .created(LocalDateTime.now())
                .build();
        itemRequest.setRequestor(1);

        when(requestService.createRequest(any(), anyInt()))
                .thenReturn(itemRequest);

        mvc.perform(post("/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemRequestNewDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequest.getId()), Integer.class))
                .andExpect(jsonPath("$.description", is(itemRequest.getDescription())))
                .andExpect(jsonPath("$.created").hasJsonPath())
                .andExpect(jsonPath("$.requestor").hasJsonPath());

        verify(requestService).createRequest(any(), anyInt());
    }

    @SneakyThrows
    @Test
    void getItems() {
        int ownerId = 1;
        when(requestService.getAllRequest(anyInt(), anyInt(), anyInt()))
                .thenReturn(itemsResponse);
        mvc.perform(get("/requests/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", ownerId)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());

        verify(requestService).getAllRequest(ownerId, 0, 10);
    }

    @SneakyThrows
    @Test
    void getItem() {
        int ownerId = 1;
        when(requestService.getRequestById(anyInt(), anyInt()))
                .thenReturn(itemResponseDto);
        mvc.perform(get("/requests/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", ownerId)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());

        verify(requestService).getAllRequest(ownerId, 0, 10);
    }

    @SneakyThrows
    @Test
    void getAllByOwner() {
        int userId = 1;

        List<ItemResponseDto> list = Collections.emptyList();

        when(requestService.getAllRequestByOwner(userId)).thenReturn(list);

        String result = mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(result, mapper.writeValueAsString(list));
    }

    @SneakyThrows
    @Test
    void getRequestById() {
        int userId = 1;
        int requestId = 1;
        ItemResponseDto itemRequestDto = ItemResponseDto.builder().build();

        when(requestService.getRequestById(userId, requestId)).thenReturn(itemRequestDto);

        String result = mvc.perform(get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(result, mapper.writeValueAsString(itemRequestDto));
    }
}
