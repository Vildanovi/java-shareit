package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
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
public class ItemRepositoryIT {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final RequestRepository requestRepository;

    private User owner;
    private User requestor;
    private Item itemByOwner;
    private Item item1ByOwner;
    private ItemRequest itemRequestByRequestor;
    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd:HH-mm");
    private final LocalDateTime currentDate = LocalDateTime.parse(LocalDateTime.now().format(dateFormat), dateFormat);

    @BeforeEach
    void setUp() {
        owner = userRepository.save(User.builder()
                .name("ownerName")
                .email("owner@mail.ru")
                .build());

        requestor = userRepository.save(User.builder()
                .name("requestorName")
                .email("requestor@mail.ru")
                .build());

        itemRequestByRequestor = requestRepository.save(ItemRequest.builder()
                .description("itemRequest")
                .requestor(requestor.getId())
                .created(currentDate)
                .build());

        itemByOwner = itemRepository.save(Item.builder()
                .name("itemName")
                .description("itemDescription")
                .available(true)
                .owner(owner)
                .request(itemRequestByRequestor.getId())
                .build());
        item1ByOwner = itemRepository.save(Item.builder()
                .name("item1Name")
                .description("item1Description")
                .available(false)
                .owner(owner)
                .request(itemRequestByRequestor.getId())
                .build());
    }

    @Test
    void findByOwner() {
        List<Item> itemResult = itemRepository.findAllByOwner_IdOrderByIdAsc(owner.getId());
        assertThat(itemResult, hasSize(2));
    }

    @Test
    void searchDescription() {
        List<Item> itemResult = itemRepository
                .findByNameContainsIgnoreCaseOrDescriptionContainsIgnoreCaseAndAvailableTrue("description", "description");
        assertThat(itemResult, hasSize(1));
    }

    @Test
    void findByRequest() {
        List<Item> itemResult = itemRepository
                .findAllByRequestIn(List.of(itemRequestByRequestor.getId()));
        assertThat(itemResult, hasSize(2));
    }
}
