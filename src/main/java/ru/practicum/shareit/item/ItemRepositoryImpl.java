package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ItemRepositoryImpl implements ItemRepository {

//    private final Map<Integer, List<Item>> items = new HashMap<>();
    private final Map<Integer, Map<Integer, Item>> itemsStorage = new HashMap<>();
    protected int generatedId = 0;

    @Override
    public List<Item> findByUserId(int userId) {
//        return new ArrayList<>(itemsStorage.get(userId).values());
        return new ArrayList<>(itemsStorage.getOrDefault(userId, Collections.emptyMap()).values());
//        return items.getOrDefault(userId, Collections.emptyList());
    }

    @Override
    public Map<Integer, Item> findMapByUserId(int userId) {
        return itemsStorage.getOrDefault(userId, Collections.emptyMap());
    }

    @Override
    public Optional<Item> findByItem(int itemId) {
        return itemsStorage.values().stream()
                .map(i -> i.get(itemId))
                .findFirst();
//        return items.entrySet()
//                .stream()
//                .flatMap(entry -> entry.getValue().stream())
//                .filter(item -> item.getId() == itemId)
//                .findFirst();
    }

    @Override
    public List<Item> searchItemsByUserId(int userId, String query) {
        return itemsStorage.get(userId).values()
                .stream()
                .filter(i -> i.getName().toLowerCase().contains(query.toLowerCase()) || i.getDescription().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
//        return items.get(userId).stream()
//                .filter(i -> i.getName().contains(query) || i.getDescription().contains(query))
//                .collect(Collectors.toList());
    }

    @Override
    public Item updateItem(int userId, int itemId, Item item) {
        return itemsStorage.get(userId).computeIfPresent(itemId, (id, i) -> item);
    }

    @Override
    public Item save(Item item) {
        itemsStorage.compute(item.getOwner(), (id, userItems) -> {
            if (userItems == null) {
                userItems = new HashMap<>();
            }
            item.setId(getItemId(item.getOwner()));
            userItems.put(item.getId(), item);
            return userItems;
        });
        return item;
//        item.setId(getId());
//        items.compute(item.getOwner(), (userId, userItems) -> {
//            if (userItems == null) {
//                userItems = new ArrayList<>();
//            }
//            userItems.add(item);
//            return userItems;
//        });
//        return item;
    }

    @Override
    public void deleteByUserIdAndItemId(int userId, int itemId) {
        if (itemsStorage.containsKey(userId)) {
            itemsStorage.get(userId).remove(itemId);
        }
//        if (items.containsKey(userId)) {
//            List<Item> userItems = items.get(userId);
//            userItems.removeIf(item -> item.getId() == itemId);
//        }
    }

    @Override
    public void deleteByUserId(int userId) {
        itemsStorage.remove(userId);
//        items.remove(userId);
    }

    private int getId() {
        return ++generatedId;
    }

    private int getItemId(int userId) {
        Map<Integer, Item> userItems = itemsStorage.get(userId);
        if (userItems == null) {
            return 1;
        } else {
            int lastId = userItems.values()
                    .stream()
                    .mapToInt(Item::getId)
                    .max()
                    .orElse(0);
            return lastId + 1;
        }
    }

//    private int getId() {
//        int lastId = items.values()
//                .stream()
//                .flatMap(Collection::stream)
//                .mapToInt(Item::getId)
//                .max()
//                .orElse(0);
//        return lastId + 1;
//    }
}
