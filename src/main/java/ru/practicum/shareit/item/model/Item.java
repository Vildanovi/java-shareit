package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * TODO Sprint add-controllers.
 */

@Data
@Builder
public class Item {
    private int id;
    @Size(max = 20)
    private String name; // краткое название
    @Size(max = 500)
    private String description; // развёрнутое описание
    private int available; // статус о том, доступна или нет вещь для аренды
    @NotBlank(message = "Вещь не может быть без владельца")
    private User owner; // владелец вещи
    // если вещь была создана по запросу другого пользователя,
    // то в этом поле будет храниться ссылка на соответствующий запрос
    private ItemRequest request;
}
