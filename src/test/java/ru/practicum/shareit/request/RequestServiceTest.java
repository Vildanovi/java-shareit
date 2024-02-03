package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.request.service.RequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RequestServiceTest {

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private RequestServiceImpl requestService;

    private User user;
    private ItemRequest itemRequest;
    private Item item;


    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1)
                .name("requestorName")
                .email("requestor@mail.ru")
                .build();
        itemRequest = ItemRequest.builder()
                .id(1)
                .requestor(1)
                .description("itemRequest")
                .created(LocalDateTime.now())
                .build();
        item = Item.builder()
                .id(1)
                .name("itemName")
                .description("itemDescription")
                .available(true)
                .request(1)
                .build();
    }

    @Test
    void create_UserExists() {
        int userId = 1;

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        requestService.createRequest(itemRequest, userId);
        verify(userRepository).findById(userId);
        verify(requestRepository).save(itemRequest);
    }

    @Test
    void create_UserNotExists() {
        int userId = 1;

        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> requestService.createRequest(itemRequest, userId));

        verify(userRepository).findById(userId);
        verify(requestRepository, never()).save(itemRequest);
    }

    @Test
    void getAllMyRequests() {
        int userId = 1;
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(user));
        when(requestRepository
                .findAllByRequestor(anyInt(), any()))
                .thenReturn(List.of(itemRequest));
        when(itemRepository.findAllByRequestIn(any()))
                .thenReturn(List.of(item));

        requestService.getAllRequestByOwner(userId);

        verify(userRepository).findById(anyInt());
        verify(requestRepository).findAllByRequestor(anyInt(), any());
        verify(itemRepository).findAllByRequestIn(any());
    }
}
