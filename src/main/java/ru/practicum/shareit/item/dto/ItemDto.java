package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import ru.practicum.shareit.request.ItemRequest;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
public class ItemDto {

    private int id;
    @NotEmpty(groups = {CreatedBy.class}, message = "Вещь не может быть без названия")
    private String name; // краткое название
    @Size(groups = {CreatedBy.class}, max = 500)
    @NotEmpty(groups = {CreatedBy.class}, message = "Вещь не может быть без описания")
    private String description; // развёрнутое описание
    @NotNull(groups = {CreatedBy.class}, message = "Вещь не может быть статуса доступности")
    private Boolean available; // статус о том, доступна или нет вещь для аренды
    private int owner; // владелец вещи
    // если вещь была создана по запросу другого пользователя,
    // то в этом поле будет храниться ссылка на соответствующий запрос
    private ItemRequest request;
}
