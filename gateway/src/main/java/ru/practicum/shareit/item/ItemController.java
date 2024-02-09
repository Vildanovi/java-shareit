package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.Collections;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getUserItems(@RequestHeader("X-Sharer-User-Id") int userId) {
        log.debug("Получаем список вещей для пользователя c id: {}", userId);
        return itemClient.getItemsWithBookingByUserId(userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@PathVariable (value = "itemId") int itemId,
                                          @RequestHeader("X-Sharer-User-Id") int userId) {
        log.debug("Получаем вещь с id: {} для пользовател: {}", itemId, userId);
        return itemClient.getItemById(itemId, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam (value = "text") String query) {
        log.debug("Ищем текст: {}", query);
        if (query.isBlank()) {
            return new ResponseEntity<>(Collections.EMPTY_LIST, HttpStatus.OK);
        }
        return itemClient.searchByText(query);
    }

    @PostMapping
    public ResponseEntity<Object> postItem(@RequestHeader("X-Sharer-User-Id") int userId,
                                       @RequestBody @Valid ItemRequestDto itemDto) {
        log.debug("Создаем вещь {} для пользователя с id: {}", itemDto, userId);
        return itemClient.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> putItem(@PathVariable @Positive int itemId,
                                          @RequestHeader("X-Sharer-User-Id") int userId,
                                          @RequestBody ItemRequestDto itemDto) {
        log.debug("Обновляем вещь c id: {} для пользователя с id: {}", itemId, userId);
        return itemClient.putItem(itemId, userId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> postComment(@PathVariable(value = "itemId") int itemId,
                                              @RequestHeader("X-Sharer-User-Id") int userId,
                                              @Validated(CreatedBy.class) @RequestBody CommentRequestDto commentDto) {
        return itemClient.createComment(userId, itemId, commentDto);
    }
}
