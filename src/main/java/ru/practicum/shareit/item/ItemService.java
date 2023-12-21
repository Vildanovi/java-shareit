package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public List<Item> getItemsByUserId(int userId) {
        userRepository.findUserById(userId);
//        userRepository.findUserById(userId)
//                .orElseThrow(() -> new EntityNotFoundException("Объект не найден: " + userId));
        return itemRepository.findByUserId(userId);
    }

    public Item getItemByUserIdItemId(int userId, int itemId) {
        userRepository.findUserById(userId);
//        return Optional.ofNullable(itemRepository.findByUserId(userId).get(itemId))
//                .orElseThrow(() -> new EntityNotFoundException("Объект не найден: " + itemId));


        List<Item> items = itemRepository.findByUserId(userId);
        return items.stream()
                .filter((i) -> (i.getId() == itemId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Объект не найден: " + itemId));

//        return itemRepository.findByUserId(userId).stream()
//                .filter(item -> item.getId() == itemId)
//                .findAny()
//                .orElseThrow(() -> new EntityNotFoundException("Объект не найден: " + itemId));
    }

    public List<Item> searchByText(int userId, String query) {
        if (query.isEmpty() || query.isBlank()) {
            return Collections.emptyList();
        } else {
            return itemRepository.searchItemsByUserId(userId, query);
        }
    }

    public Item createItem(int userId, Item item) {
        userRepository.findUserById(userId);
//        userRepository.findUserById(userId)
//                .orElseThrow(() -> new EntityNotFoundException("Объект не найден: " + userId));
        item.setOwner(userId);
        return itemRepository.save(item);
    }

    public Item putItem(int itemId, int userId, Item item) {
        Item updateItem = getItemByUserIdItemId(userId, itemId);
//        Item updateItem = itemRepository.findByItem(itemId)
//                .orElseThrow(() -> new EntityNotFoundException("Объект не найден: " + itemId));
//        userRepository.findUserById(userId)
//                .orElseThrow(() -> new EntityNotFoundException("Объект не найден: " + userId));
        String name = item.getName();
        String description = item.getDescription();
        Boolean available = item.getAvailable();
        if (name != null) {
            updateItem.setName(name);
        }
        if (description != null) {
            updateItem.setDescription(description);
        }
        if (available != null) {
            updateItem.setAvailable(available);
        }
        List<Item> userItems = itemRepository.findByUserId(userId);
        Item currentItem = itemRepository.findByUserId(userId).stream()
                .filter(item1 -> item1.getId() == itemId)
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Объект не найден: " + itemId));
        if (currentItem != null) {
            userItems.set(userItems.indexOf(currentItem), updateItem);
        }
//        userItems.stream()
//                .filter(i -> i.getId() == itemId)
//                .findFirst()
//                .ifPresent(userItem -> userItems.set(userItems.indexOf(userItem), updateItem));
        return updateItem;
    }

    public Item deleteItem(int itemId) {
        Item deleteItem = itemRepository.findByItem(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Объект не найден: " + itemId));
        itemRepository.deleteByUserIdAndItemId(deleteItem.getOwner(), itemId);
        return deleteItem;
    }

//    private Boolean isValidCreatedItem(Item item) {
//
//    }
}
