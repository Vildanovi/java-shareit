package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ItemRepositoryImpl implements ItemRepository {

    private final Map<Integer, List<Item>> items = new HashMap<>();

    @Override
    public List<Item> findByUserId(int userId) {
        return items.getOrDefault(userId, Collections.emptyList());
    }

    @Override
    public Optional<Item> findByItem(int itemId) {
        return items.entrySet()
                .stream()
                .flatMap(entry -> entry.getValue().stream())
                .filter(item -> item.getId() == itemId)
                .findFirst();
    }

    @Override
    public List<Item> searchItemsByUserId(int userId, String query) {
        return items.get(userId).stream()
                .filter(i -> i.getName().contains(query) || i.getDescription().contains(query))
                .collect(Collectors.toList());
    }

    @Override
    public Item save(Item item) {
        item.setId(getId());
        items.compute(item.getOwner(), (userId, userItems) -> {
            if (userItems == null) {
                userItems = new ArrayList<>();
            }
            userItems.add(item);
            return userItems;
        });
        return item;
    }

    @Override
    public void deleteByUserIdAndItemId(int userId, int itemId) {
        if (items.containsKey(userId)) {
            List<Item> userItems = items.get(userId);
            userItems.removeIf(item -> item.getId() == itemId);
        }
    }

    @Override
    public void deleteByUserId(int userId) {
        items.remove(userId);
    }

    private int getId() {
        int lastId = items.values()
                .stream()
                .flatMap(Collection::stream)
                .mapToInt(Item::getId)
                .max()
                .orElse(0);
        return lastId + 1;
    }
}
