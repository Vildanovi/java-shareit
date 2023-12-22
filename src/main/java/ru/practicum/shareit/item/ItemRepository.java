package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;
import java.util.List;

public interface ItemRepository {

    List<Item> findByUserId(int userId);

    Item save(Item item);

    void deleteByUserIdAndItemId(int userId, int itemId);

    void deleteByUserId(int userId);

    Item findByItem(int itemId);

    List<Item> searchItemsByUserId(String query);

    Item updateItem(int userId, int itemId, Item item);
}
