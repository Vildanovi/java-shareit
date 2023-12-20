package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public List<Item> getItemsByUserId(int userId) {
        log.debug("Получаем список вещей для пользователя c id: {}", userId);
        userRepository.findUserById(userId);
//        userRepository.findUserById(userId)
//                .orElseThrow(() -> new EntityNotFoundException("Объект не найден: " + userId));
        return itemRepository.findByUserId(userId);
    }

    public Item getItemByUserIdItemId(int userId, int itemId) {
        log.debug("Ищем вещь с id: {} для пользователя с id: {}", itemId, userId);
        return itemRepository.findByUserId(userId).stream()
                .filter(item -> item.getId() == itemId)
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Объект не найден: " + itemId));
    }

    public List<Item> searchByText(int userId, String query) {
        log.debug("Ищем текст: {} для пользователя с id: {}", query, userId);
        if (query.isEmpty() || query.isBlank()) {
            return Collections.emptyList();
        } else {
            return itemRepository.searchItemsByUserId(userId, query);
        }
    }

    public Item createItem(int userId, Item item) {
        log.debug("Создаем вещь {} для пользователя с id: {}", item, userId);
        userRepository.findUserById(userId);
//        userRepository.findUserById(userId)
//                .orElseThrow(() -> new EntityNotFoundException("Объект не найден: " + userId));
        item.setOwner(userId);
        return itemRepository.save(item);
    }

    public Item putItem(int itemId, int userId, Item item) {
        log.debug("Обновляем вещь c id: {} для пользователя с id: {}", itemId, userId);
        Item updateItem = itemRepository.findByItem(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Объект не найден: " + itemId));
        userRepository.findUserById(userId);
//        userRepository.findUserById(userId)
//                .orElseThrow(() -> new EntityNotFoundException("Объект не найден: " + userId));
        String name = item.getName();
        String description = item.getDescription();
        String available = item.getAvailable();
        if (!name.isEmpty()) {
            updateItem.setName(name);
        }
        if (!description.isEmpty()) {
            updateItem.setDescription(description);
        }
        if (available.equals("true") || available.equals("false")) {
            updateItem.setAvailable(available);
        }
        List<Item> userItems = itemRepository.findByUserId(userId);
        userItems.stream()
                .filter(i -> i.getId() == itemId)
                .findFirst().ifPresent(userItem -> userItems.set(userItems.indexOf(userItem), updateItem));
        return updateItem;
    }

    public Item deleteItem(int itemId) {
        log.debug("Удаляем вещь с id: {}", itemId);
        Item deleteItem = itemRepository.findByItem(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Объект не найден: " + itemId));
        itemRepository.deleteByUserIdAndItemId(deleteItem.getOwner(), itemId);
        return deleteItem;
    }
}