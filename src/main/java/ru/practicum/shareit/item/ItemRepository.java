package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;
import java.util.List;
import java.util.Optional;

public interface ItemRepository {

    List<Item> findByUserId(int userId);

    Item save(Item item);

    void deleteByUserIdAndItemId(int userId, int itemId);

    void deleteByUserId(int userId);

    Optional<Item> findByItem(int itemId);

    List<Item> searchItemsByUserId(int userId, String query);
}