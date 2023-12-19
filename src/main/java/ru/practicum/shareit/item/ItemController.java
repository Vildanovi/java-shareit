package ru.practicum.shareit.item;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemService itemService;
    private final UserService userService;

    @GetMapping
    @Operation(summary = "Получить список вещей пользователя")
    public List<Item> getUserItems(@RequestHeader("X-Sharer-User-Id") int userId) {
        return itemService.getItemsByUserId(userId);
    }

    @GetMapping("/{itemId}")
    @Operation(summary = "Получение вещи по идентификатору")
    public Item getItem(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable ("itemId") int itemId) {
        return itemService.getItemByUserIdItemId(userId, itemId);
    }

    @GetMapping("/search")
    @Operation(summary = "Поиск вещей пользователя по наименованию и описанию")
    public List<Item> searchItems(@RequestHeader("X-Sharer-User-Id") int userId, @RequestParam (value = "text") String query) {
        return itemService.searchByText(userId, query);
    }

    @PostMapping
    @Operation(summary = "Добавление вещи для пользователя")
    public Item postItem(@RequestHeader("X-Sharer-User-Id") int userId, @RequestBody Item item) {
        return itemService.createItem(userId, item);
    }

    @PatchMapping
    @Operation(summary = "Обновление параметров вещи")
    public Item putItem(@RequestParam(value = "itemId") int itemId,
                        @RequestHeader("X-Sharer-User-Id") int userId,
                        @RequestBody Item item) {
        return itemService.putItem(itemId, userId, item);
    }

    @DeleteMapping("/{itemId}")
    @Operation(summary = "Удаление вещи для пользователя")
    public Item deleteItem(@PathVariable ("itemId") int itemId) {
        return itemService.deleteItem(itemId);
    }
}
