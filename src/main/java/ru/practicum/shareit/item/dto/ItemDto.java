package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import javax.validation.constraints.Size;

@Data
@Builder
public class ItemDto {
    private int id;
    @Size(max = 20)
    private String name; // краткое название
    @Size(max = 500)
    private String description; // развёрнутое описание
    private Boolean available; // статус о том, доступна или нет вещь для аренды
    // если вещь была создана по запросу другого пользователя,
    // то в этом поле будет храниться ссылка на соответствующий запрос
}
