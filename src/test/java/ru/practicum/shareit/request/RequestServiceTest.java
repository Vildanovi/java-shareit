package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@AutoConfigureTestDatabase
@SpringBootTest
@Transactional
@Rollback
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RequestServiceTest {

    private final UserService userService;
    private final RequestService requestService;

    private ItemRequest itemRequestByRequestor;
    private ItemRequest itemRequest1ByRequestor;
    private ItemRequest itemRequest2ByRequestor;
    private User requestor;
    private User requestor1;

    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd:HH-mm");
    private final LocalDateTime currentDate = LocalDateTime.parse(LocalDateTime.now().format(dateFormat), dateFormat);

    @BeforeEach
    void setUp() {
        requestor = User.builder()
                .name("requestorName")
                .email("requestor@mail.ru")
                .build();
        requestor1 = User.builder()
                .name("requestor1Name")
                .email("requestor1@mail.ru")
                .build();
        requestor = userService.createUser(requestor);
        requestor1 = userService.createUser(requestor1);
        itemRequestByRequestor = ItemRequest.builder()
                .description("itemRequest")
                .created(currentDate)
                .build();
        itemRequest1ByRequestor = ItemRequest.builder()
                .description("itemRequest1")
                .created(currentDate)
                .build();
        itemRequest2ByRequestor = ItemRequest.builder()
                .description("itemRequest2")
                .created(currentDate)
                .build();

        itemRequestByRequestor = requestService.createRequest(itemRequestByRequestor, requestor.getId());
        itemRequest1ByRequestor = requestService.createRequest(itemRequest1ByRequestor, requestor1.getId());
        itemRequest2ByRequestor = requestService.createRequest(itemRequest2ByRequestor, requestor1.getId());
    }

    @Test
    void getAllByOwner() {
        List<ItemResponseDto> requests = requestService.getAllRequestByOwner(requestor1.getId());
        assertThat(requests, hasSize(2));
    }

    @Test
    void getAll() {
        List<ItemResponseDto> requests = requestService.getAllRequest(requestor1.getId(), 0, 10);
        assertThat(requests, hasSize(1));
    }

    @Test
    void getById() {
        ItemResponseDto request = requestService.getRequestById(requestor1.getId(), itemRequest1ByRequestor.getId());
        MatcherAssert.assertThat(request.getDescription(), equalTo(itemRequest1ByRequestor.getDescription()));

    }



//    ItemResponseDto getRequestById(int userId, int requestId);
}
