package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.enumerations.BookingStatus;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ValidationBadRequestException;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dto.ItemResponseWithBookingDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    public List<ItemResponseWithBookingDto> getItemsWithBookingByUserId(int userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Объект не найден: " + userId));
        List<ItemResponseWithBookingDto> allItems = itemRepository.findAllByOwner_Id(userId)
                .stream()
                .map(ItemMapper::mapItemToResponseWithBooking)
                .collect(toList());
        List<BookingForItemDto> bookings = bookingRepository.findAllByItem_Owner_IdOrderByStartAsc(userId)
                .stream()
                .map(BookingMapper::mapBookingToBookingForItem)
                .collect(toList());
        List<Integer> itemIds = allItems.stream()
                .map(ItemResponseWithBookingDto::getId)
                .collect(toList());
        List<Comment> comments = commentRepository.findByItem_IdIn(itemIds);
        List<ItemResponseWithBookingDto> itemsWithBookings = new ArrayList<>();

        for (ItemResponseWithBookingDto item : allItems) {
            LocalDateTime now = LocalDateTime.now();
            int itemId = item.getId();
            item.setLastBooking(bookings
                    .stream()
                    .filter(b -> b.getItemId() == itemId)
                    .filter(b -> !b.getStart().isAfter(now))
                    .reduce((first, second) -> second)
                    .orElse(null));
            item.setNextBooking(bookings
                    .stream()
                    .filter(b -> b.getItemId() == itemId)
                    .filter(b -> b.getStart().isAfter(now))
                    .findFirst()
                    .orElse(null));
            item.setComments(comments
                    .stream()
                    .filter(c -> c.getItem().getId() == itemId).map(CommentMapper::mapCommentToCommentForItem)
                    .collect(toList()));
            itemsWithBookings.add(item);
        }
        return itemsWithBookings;
    }

    public ItemResponseWithBookingDto getItemById(int itemId, int userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Объект не найден: " + userId));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Объект не найден: " + itemId));
        Booking lastBooking3 = bookingRepository.findFirstByItem_Owner_IdAndStatusAndStartLessThanEqualOrderByEndDesc(userId, BookingStatus.APPROVED, LocalDateTime.now());
        Booking nextBooking = bookingRepository.findFirstByItem_IdAndItem_Owner_IdAndStatusAndStartIsAfterOrderByStartAsc(itemId, userId, BookingStatus.APPROVED, LocalDateTime.now());

        List<Comment> itemComments = commentRepository.findAllByItem_Id(itemId);

        ItemResponseWithBookingDto itemWithBooking = ItemMapper.mapItemToResponseWithBooking(item);

        if (lastBooking3 != null) {
            itemWithBooking.setLastBooking(BookingMapper.mapBookingToBookingForItem(lastBooking3));
        }
        if (nextBooking != null) {
            itemWithBooking.setNextBooking(BookingMapper.mapBookingToBookingForItem(nextBooking));
        }
        if (!itemComments.isEmpty()) {
            itemWithBooking.setComments(itemComments
                    .stream()
                    .map(CommentMapper::mapCommentToCommentForItem)
                    .collect(toList()));
        } else {
            itemWithBooking.setComments(Collections.emptyList());
        }

        return itemWithBooking;
    }

    public List<Item> searchByText(String query) {
        if (query.isBlank()) {
            return Collections.emptyList();
        } else {
            return itemRepository
                    .findByNameContainsIgnoreCaseOrDescriptionContainsIgnoreCaseAndAvailableTrue(query, query);
        }
    }

    @Transactional
    public Item createItem(int userId, Item item) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Объект не найден: " + userId));
        item.setOwner(user);
        return itemRepository.save(item);
    }

    @Transactional
    public Item putItem(int itemId, int userId, Item item) {
        Item updateItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Объект не найден: " + itemId));
        if (updateItem.getOwner().getId() == userId) {
            String name = item.getName();
            String description = item.getDescription();
            Boolean available = item.getAvailable();
            if (name != null && !name.isBlank()) {
                updateItem.setName(name);
            }
            if (description != null && !description.isBlank()) {
                updateItem.setDescription(description);
            }
            if (available != null) {
                updateItem.setAvailable(available);
            }
            return updateItem;
        } else {
            throw new EntityNotFoundException("Не является владельцем: " + itemId);
        }
    }

    @Transactional
    public Item deleteItem(int itemId) {
        Item deleteItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Объект не найден: " + itemId));
        itemRepository.deleteById(itemId);
        return deleteItem;
    }

    @Transactional
    public Comment createComment(int itemId, int userId, Comment comment) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Объект не найден: " + userId));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Объект не найден: " + userId));
        Booking bookingFinish = bookingRepository
                .findAllByBooker_IdAndItem_IdAndEndIsBeforeOrderByStartDesc(userId, itemId, LocalDateTime.now())
                .stream()
                .findFirst()
                .orElse(null);
        if (bookingFinish == null) {
            throw new ValidationBadRequestException("Нет завершенных бронирований");
        }
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(Instant.now());
        return commentRepository.save(comment);
    }
}