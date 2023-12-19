package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Component
public class ItemRepositoryImpl implements ItemRepository{

    private final Map<Integer, List<Item>> items = new HashMap<>();

    @Override
    public List<Item> findByUserId(int userId) {
        return items.getOrDefault(userId, Collections.emptyList());
    }

    @Override
    public Item save(Item item) {
        item.setId(getId());
        items.compute(item.getOwner(), (userId, userItems) -> {
            if(userItems == null) {
                userItems = new ArrayList<>();
            }
            userItems.add(item);
            return userItems;
        });

        return item;
    }

    @Override
    public void deleteByUserIdAndItemId(int userId, int ItemId) {
        if(items.containsKey(userId)) {
            List<Item> userItems = items.get(userId);
            userItems.removeIf(item -> item.getId() == ItemId);
        }
    }

    @Override
    public void deleteByUserId(int userId) {
        items.remove(userId);
    }

    @Override
    public Optional<Item> findByItem(int itemId) {
        return items.entrySet()
                .stream()
                .flatMap(entry -> entry.getValue().stream())
                .filter(item -> item.getId() == itemId)
                .findFirst();
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
