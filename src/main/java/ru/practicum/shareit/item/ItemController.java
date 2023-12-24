package ru.practicum.shareit.item;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    @Operation(summary = "Получить список вещей пользователя")
    public List<ItemResponseDto> getUserItems(@RequestHeader(Constants.USER_ID) int userId) {
        log.debug("Получаем список вещей для пользователя c id: {}", userId);
        return itemService.getItemsByUserId(userId)
                .stream()
                .map(ItemMapper::itemResponseDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{itemId}")
    @Operation(summary = "Получить вещи по идентификатору")
    public ItemResponseDto getItem(@PathVariable ("itemId") int itemId) {
        log.debug("Получаем вещь с id: {}", itemId);
        return ItemMapper.itemResponseDto(itemService.getItemById(itemId));
    }

    @GetMapping("/search")
    @Operation(summary = "Поиск вещей пользователя по наименованию и описанию")
    public List<ItemResponseDto> searchItems(@RequestParam (value = "text") String query) {
        log.debug("Ищем текст: {}", query);
        return itemService.searchByText(query)
                .stream()
                .map(ItemMapper::itemResponseDto)
                .collect(Collectors.toList());
    }

    @PostMapping
    @Operation(summary = "Добавление вещи для пользователя")
    public ItemResponseDto postItem(@RequestHeader(Constants.USER_ID) int userId,
                                    @Validated(CreatedBy.class) @RequestBody ItemDto itemDto) {
        log.debug("Создаем вещь {} для пользователя с id: {}", itemDto, userId);
        Item item = ItemMapper.toItem(itemDto);
        return ItemMapper.itemResponseDto(itemService.createItem(userId, item));
    }

    @PatchMapping("/{itemId}")
    @Operation(summary = "Обновление параметров вещи")
    public ItemResponseDto putItem(@PathVariable (value = "itemId") int itemId,
                        @RequestHeader(Constants.USER_ID) int userId,
                        @RequestBody ItemDto itemDto) {
        log.debug("Обновляем вещь c id: {} для пользователя с id: {}", itemId, userId);
        Item item = ItemMapper.toItem(itemDto);
        return ItemMapper.itemResponseDto(itemService.putItem(itemId, userId, item));
    }

    @DeleteMapping("/{itemId}")
    @Operation(summary = "Удаление вещи для пользователя")
    public ItemResponseDto deleteItem(@PathVariable ("itemId") int itemId) {
        log.debug("Удаляем вещь с id: {}", itemId);
        return ItemMapper.itemResponseDto(itemService.deleteItem(itemId));
    }
}
