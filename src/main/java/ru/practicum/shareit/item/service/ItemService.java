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
import java.util.stream.Collectors;

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
        List<ItemResponseWithBookingDto> items = itemRepository.findAll()
                .stream()
                .filter(item -> item.getOwner() == userId)
                .map(ItemMapper::mapItemToResponseWithBooking)
                .collect(Collectors.toList());
        List<BookingForItemDto> bookings = bookingRepository.findAllByItem_Owner(userId)
                .stream()
                .map(BookingMapper::mapBookingToBookingForItem)
                .collect(Collectors.toList());
        List<ItemResponseWithBookingDto> itemsWithBookings = new ArrayList<>();
        for(ItemResponseWithBookingDto item : items) {
            LocalDateTime currentDateTime = LocalDateTime.now();
            Set<BookingForItemDto> lastBooking = new TreeSet<>(Comparator.comparing(BookingForItemDto::getEnd));
            Set<BookingForItemDto> nextBooking = new TreeSet<>(Comparator.comparing(BookingForItemDto::getStart));
            for(BookingForItemDto booking : bookings) {
                if(item.getId() == booking.getItemId()) {
                    if(booking.getStart().isBefore(currentDateTime)) {
                        lastBooking.add(booking);
                    } else {
                        nextBooking.add(booking);
                    }
                }
            }
            item.setLastBooking(lastBooking.stream().findFirst().orElse(null));
            item.setNextBooking(nextBooking.stream().findFirst().orElse(null));
            itemsWithBookings.add(item);
        }
        return itemsWithBookings;
    }

    public ItemResponseWithBookingDto getItemById(int itemId, int userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Объект не найден: " + userId));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Объект не найден: " + itemId));

        Booking lastBooking3 = bookingRepository.findAllByItem_OwnerAndStartIsBeforeOrderByEndDesc(userId, LocalDateTime.now())
                .stream()
                .filter(booking -> booking.getStatus() != BookingStatus.REJECTED)
                .findFirst().orElse(null);

        Booking nextBooking = bookingRepository.findAllByItem_IdAndItem_OwnerAndStartIsAfterOrderByStartAsc(itemId, userId, LocalDateTime.now())
                .stream()
                .findFirst().orElse(null);;

        List<Comment> itemComments = commentRepository.findAllByItem_Id(itemId);

        ItemResponseWithBookingDto itemWithBooking = ItemMapper.mapItemToResponseWithBooking(item);

        if(lastBooking3 != null) {
            itemWithBooking.setLastBooking(BookingMapper.mapBookingToBookingForItem(lastBooking3));
        }
        if(nextBooking != null && nextBooking.getStatus() != BookingStatus.REJECTED) {
            itemWithBooking.setNextBooking(BookingMapper.mapBookingToBookingForItem(nextBooking));
        }

        if(!itemComments.isEmpty()) {
            itemWithBooking.setComments(itemComments
                    .stream()
                    .map(CommentMapper::mapCommentToCommentForItem)
                    .collect(Collectors.toList()));
        } else {
            itemWithBooking.setComments(new ArrayList<>());
        }

        return itemWithBooking;
    }

    public List<Item> searchByText(String query) {
        if (query.isBlank()) {
            return Collections.emptyList();
        } else {
            return itemRepository
                    .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailable(query, query, true);
        }
    }

    @Transactional
    public Item createItem(int userId, Item item) {
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Объект не найден: " + userId));
        item.setOwner(userId);
        return itemRepository.save(item);
    }

    @Transactional
    public Item putItem(int itemId, int userId, Item item) {
        Item updateItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Объект не найден: " + itemId));
        if (updateItem.getOwner() == userId) {
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
            return itemRepository.save(updateItem);
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
        if(bookingFinish == null) {
            throw new ValidationBadRequestException("Нет завершенных бронирований");
        }
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(Instant.now());
        return commentRepository.save(comment);
    }
}