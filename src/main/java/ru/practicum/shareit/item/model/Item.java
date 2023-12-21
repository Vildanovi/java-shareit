package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.request.ItemRequest;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * TODO Sprint add-controllers.
 */

@Data
@Builder
public class Item {

    private int id;
//    @Size(max = 20)
    @NotBlank(message = "Вещь не может быть без названия")
    private String name; // краткое название
    @Size(max = 500)
    @NotBlank(message = "Вещь не может быть без описания")
    private String description; // развёрнутое описание
    @NotNull(message = "Вещь не может быть статуса доступности")
    private Boolean available; // статус о том, доступна или нет вещь для аренды
//    @NotBlank(message = "Вещь не может быть без владельца")
    private int owner; // владелец вещи
    // если вещь была создана по запросу другого пользователя,
    // то в этом поле будет храниться ссылка на соответствующий запрос
    private ItemRequest request;
}
