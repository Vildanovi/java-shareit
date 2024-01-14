package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.constant.Constants;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public List<Item> getItemsByUserId(int userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Объект не найден: " + userId));
        return itemRepository.findAll()
                .stream()
                .filter(item -> item.getOwner() == userId)
                .collect(Collectors.toList());
    }

    public Item getItemById(int itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Объект не найден: " + itemId));
    }

    public List<Item> searchByText(String query) {
        if (query.isBlank()) {
            return Collections.emptyList();
        } else {
            return itemRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailable(query, query, true);
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
}