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
        userRepository.findUserById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Объект не найден: " + userId));
        return itemRepository.findByUserId(userId);
    }

    public Item getItemById(int itemId) {
        return itemRepository.findByItem(itemId);
    }

    public List<Item> searchByText(String query) {
        if (query.isBlank()) {
            return Collections.emptyList();
        } else {
            return itemRepository.searchItemsByUserId(query);
        }
    }

    public Item createItem(int userId, Item item) {
        userRepository.findUserById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Объект не найден: " + userId));
        item.setOwner(userId);
        return itemRepository.save(item);
    }

    public Item putItem(int itemId, int userId, Item item) {
        return itemRepository.updateItem(userId, itemId, item);
    }

    public Item deleteItem(int itemId) {
        Item deleteItem = itemRepository.findByItem(itemId);
        itemRepository.deleteByUserIdAndItemId(deleteItem.getOwner(), itemId);
        return deleteItem;
    }
}