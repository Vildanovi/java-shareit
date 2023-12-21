package ru.practicum.shareit.item;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    @Operation(summary = "Получить список вещей пользователя")
    public List<Item> getUserItems(@RequestHeader("X-Sharer-User-Id") int userId) {
        log.debug("Получаем список вещей для пользователя c id: {}", userId);
        return itemService.getItemsByUserId(userId);
    }

    @GetMapping("/{itemId}")
    @Operation(summary = "Получить вещи по идентификатору")
    public Item getItem(@RequestHeader("X-Sharer-User-Id") int userId,
                        @PathVariable ("itemId") int itemId) {
        log.debug("Получаем вещь с id: {} для пользователя с id: {}", itemId, userId);
        return itemService.getItemByUserIdItemId(userId, itemId);
    }

    @GetMapping("/search")
    @Operation(summary = "Поиск вещей пользователя по наименованию и описанию")
    public List<Item> searchItems(@RequestHeader("X-Sharer-User-Id") int userId,
                                  @RequestParam (value = "text") String query) {
        log.debug("Ищем текст: {} для пользователя с id: {}", query, userId);
        return itemService.searchByText(userId, query);
    }

    @PostMapping
    @Operation(summary = "Добавление вещи для пользователя")
    public ResponseEntity<Item> postItem(@RequestHeader("X-Sharer-User-Id") int userId,
                         @Valid @RequestBody Item item) {
        log.debug("Создаем вещь {} для пользователя с id: {}", item, userId);
        return ResponseEntity.ok(itemService.createItem(userId, item));
    }

    @PatchMapping("/{itemId}")
    @Operation(summary = "Обновление параметров вещи")
    public Item putItem(@PathVariable (value = "itemId") int itemId,
                        @RequestHeader("X-Sharer-User-Id") int userId,
                        @RequestBody Item item) {
        log.debug("Обновляем вещь c id: {} для пользователя с id: {}", itemId, userId);
        return itemService.putItem(itemId, userId, item);
    }

    @DeleteMapping("/{itemId}")
    @Operation(summary = "Удаление вещи для пользователя")
    public Item deleteItem(@PathVariable ("itemId") int itemId) {
        log.debug("Удаляем вещь с id: {}", itemId);
        return itemService.deleteItem(itemId);
    }
}
