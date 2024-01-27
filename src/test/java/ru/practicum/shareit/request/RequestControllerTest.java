package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestNewDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.RequestServiceImpl;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({ItemRequestController.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RequestControllerTest {

    private final MockMvc mvc;
    private final ObjectMapper mapper;

    @MockBean
    private RequestServiceImpl requestService;

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
}
