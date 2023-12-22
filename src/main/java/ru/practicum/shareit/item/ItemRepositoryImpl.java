package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.model.Item;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Integer, Map<Integer, Item>> itemsStorage = new HashMap<>();
    protected int generatedId = 0;

    @Override
    public List<Item> findByUserId(int userId) {
        return new ArrayList<>(itemsStorage.getOrDefault(userId, Collections.emptyMap()).values());
    }

    @Override
    public Item findByItem(int itemId) {
        return itemsStorage.values()
                .stream()
                .flatMap(map -> map.values().stream())
                .collect(Collectors.toList())
                .stream()
                .filter(item -> item.getId() == itemId)
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Объект не найден: " + itemId));
    }

    @Override
    public List<Item> searchItemsByUserId(String query) {
        return itemsStorage.values()
                .stream()
                .flatMap(map -> map.values().stream())
                .collect(Collectors.toList())
                .stream()
                .filter(i -> i.getName().toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT))
                        || i.getDescription().toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT)))
                .filter(Item::getAvailable)
                .collect(Collectors.toList());
    }

    @Override
    public Item updateItem(int userId, int itemId, Item item) {
        Item updateItem = findByItem(itemId);
        if (updateItem.getOwner() == userId) {
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
            itemsStorage.get(userId).put(itemId, updateItem);
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
            item.setId(getId());
            userItems.put(item.getId(), item);
            return userItems;
        });
        return item;
    }

    @Override
    public void deleteByUserIdAndItemId(int userId, int itemId) {
        if (itemsStorage.containsKey(userId)) {
            itemsStorage.get(userId).remove(itemId);
        }
    }

    @Override
    public void deleteByUserId(int userId) {
        itemsStorage.remove(userId);
    }

    private int getId() {
        return ++generatedId;
    }
}
