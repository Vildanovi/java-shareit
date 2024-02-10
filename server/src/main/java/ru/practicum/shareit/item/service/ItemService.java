package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
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
import static java.util.stream.Collectors.groupingBy;
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
        List<ItemResponseWithBookingDto> allItems = itemRepository.findAllByOwner_IdOrderByIdAsc(userId)
                .stream()
                .map(ItemMapper::mapItemToResponseWithBooking)
                .collect(toList());
        List<Integer> itemIds = allItems.stream()
                .map(ItemResponseWithBookingDto::getId)
                .collect(toList());

        Map<Integer, List<Booking>> itemsBookings = bookingRepository
                .findAllByItem_IdIn(itemIds, Sort.by(Sort.Direction.ASC, "start"))
                .stream()
                .collect(groupingBy(b -> b.getItem().getId(), toList()));
        Map<Integer, List<Comment>> cItem = commentRepository.findByItem_IdIn(itemIds)
                .stream()
                .collect(groupingBy(Comment::getId, toList()));

        for (ItemResponseWithBookingDto item : allItems) {
            LocalDateTime now = LocalDateTime.now();
            List<Booking> itemBookings = itemsBookings.getOrDefault(item.getId(), null);
            if (itemBookings != null) {
                List<BookingForItemDto> bookingResult = itemBookings
                        .stream()
                        .map(BookingMapper::mapBookingToBookingForItem)
                        .collect(toList());
                item.setLastBooking(bookingResult
                        .stream()
                        .filter(b -> !b.getStart().isAfter(now))
                        .reduce((first, second) -> second).orElse(null));
                item.setNextBooking(bookingResult
                        .stream()
                        .filter(b -> b.getStart().isAfter(now))
                        .findFirst()
                        .orElse(null));
            }
            List<Comment> commentResult = cItem.getOrDefault(item.getId(), null);
            if (commentResult != null) {
                item.setComments(commentResult
                        .stream()
                        .map(CommentMapper::mapCommentToCommentForItem)
                        .collect(toList()));
            }
        }
        return allItems;
    }

    public ItemResponseWithBookingDto getItemById(int itemId, int userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Объект не найден: " + userId));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Объект не найден: " + itemId));
        Booking lastBooking = null;
        Booking nextBooking = null;
        if (Objects.equals(item.getOwner().getId(), userId)) {
            lastBooking = bookingRepository
                    .findFirstByItem_IdAndStatusAndStartLessThanEqualOrderByEndDesc(itemId, BookingStatus.APPROVED, LocalDateTime.now());
            nextBooking = bookingRepository
                    .findFirstByItem_IdAndStatusAndStartIsAfterOrderByStartAsc(itemId, BookingStatus.APPROVED, LocalDateTime.now());
        }
        List<Comment> itemComments = commentRepository.findAllByItem_Id(itemId);
        ItemResponseWithBookingDto itemWithBooking = ItemMapper.mapItemToResponseWithBooking(item);
        if (lastBooking != null) {
            itemWithBooking.setLastBooking(BookingMapper.mapBookingToBookingForItem(lastBooking));
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
        boolean booking = bookingRepository
                .existsByBooker_IdAndItem_IdAndEndIsBeforeOrderByStartDesc(userId, itemId, LocalDateTime.now());
        if (!booking) {
            throw new ValidationBadRequestException("Нет завершенных бронирований");
        }
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(Instant.now());
        return commentRepository.save(comment);
    }
}