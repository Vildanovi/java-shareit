package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<Item> getItems(@RequestHeader("X-Sharer-User-Id") int userId) {
        return itemService.getItemsByUserId(userId);
    }

    @PutMapping
    public Item postItem(@RequestHeader("X-Sharer-User-Id") int userId, @RequestBody Item item) {
        return itemService.createItem(userId, item);
    }
}
