package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.model.Item;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Integer, Map<Integer, Item>> itemsStorage = new HashMap<>();
    private final Map<Integer, Item> items = new HashMap<>();

    protected int generatedId = 0;

    @Override
    public List<Item> findByUserId(int userId) {
        return new ArrayList<>(itemsStorage.getOrDefault(userId, Collections.emptyMap()).values());
    }

    @Override
    public Item findByItem(int itemId) {
        if (items.containsKey(itemId)) {
            return items.get(itemId);
        } else {
            throw new EntityNotFoundException("Объект не найден: " + itemId);
        }
    }

    @Override
    public List<Item> searchItemsByUserId(String query) {
        String searchQuery = query.toLowerCase(Locale.ROOT);
        return items.values()
                .stream()
                .filter(i -> i.getName().toLowerCase(Locale.ROOT).contains(searchQuery)
                        || i.getDescription().toLowerCase(Locale.ROOT).contains(searchQuery) && i.getAvailable())
                .collect(Collectors.toList());
    }

    @Override
    public Item updateItem(int userId, int itemId, Item item) {
        Item updateItem = findByItem(itemId);
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
            return updateItem;
        } else {
            throw new EntityNotFoundException("Объект не найден: " + itemId);
        }
    }

    @Override
    public Item save(Item item) {
        itemsStorage.compute(item.getOwner(), (id, userItems) -> {
            if (userItems == null) {
                userItems = new HashMap<>();
            }
            int itemId = getId();
            item.setId(itemId);
            userItems.put(itemId, item);
            items.put(itemId, item);
            return userItems;
        });
        return item;
    }

    @Override
    public void deleteByUserIdAndItemId(int userId, int itemId) {
        if (itemsStorage.containsKey(userId)) {
            itemsStorage.get(userId).remove(itemId);
            items.remove(itemId);
        }
    }

    @Override
    public void deleteByUserId(int userId) {
        Map<Integer, Item> deleteMap = itemsStorage.remove(userId);
        if (deleteMap != null) {
            deleteMap.forEach((k, v) -> items.remove(k));
        }
    }

    private int getId() {
        return ++generatedId;
    }
}
