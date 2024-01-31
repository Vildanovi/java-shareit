package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RequestRepositoryIT {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    private ItemRequest itemRequestByRequestor;
    private ItemRequest itemRequest1ByRequestor;
    private ItemRequest itemRequest2ByRequestor;
    private User requestor;
    private User requestor1;

    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd:HH-mm");
    private final LocalDateTime currentDate = LocalDateTime.parse(LocalDateTime.now().format(dateFormat), dateFormat);

    @BeforeEach
    void setUp() {
        requestor = userRepository.save(User.builder()
                .name("requestorName")
                .email("requestor@mail.ru")
                .build());
        requestor1 = userRepository.save(User.builder()
                .name("requestor1Name")
                .email("requestor1@mail.ru")
                .build());
        itemRequestByRequestor = requestRepository.save(ItemRequest.builder()
                .description("itemRequest")
                .requestor(requestor.getId())
                .created(currentDate)
                .build());
        itemRequest1ByRequestor = requestRepository.save(ItemRequest.builder()
                .description("itemRequest")
                .requestor(requestor1.getId())
                .created(currentDate)
                .build());
        itemRequest2ByRequestor = requestRepository.save(ItemRequest.builder()
                .description("itemRequest")
                .requestor(requestor1.getId())
                .created(currentDate)
                .build());
    }

    @AfterEach
    void cleanRepositories() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void findByRequestor() {
        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        List<ItemRequest> requestResult = requestRepository.findAllByRequestor(requestor.getId(), sort);
        assertThat(requestResult, hasSize(1));
    }

    @Test
    void findByRequestWithoutRequestor() {
        Pageable page = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "created"));
        List<ItemRequest> requestResult = requestRepository.findAllByRequestorNot(requestor.getId(), page);
        assertThat(requestResult, hasSize(2));
    }


}
