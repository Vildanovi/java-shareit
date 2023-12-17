package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {

    Optional<List<Item>> findByUserId(int userId);
    Item save(Item item);
    void deleteByUserIdAndItemId(int userId, int ItemId);
    void deleteByUserId(int userId);
}
