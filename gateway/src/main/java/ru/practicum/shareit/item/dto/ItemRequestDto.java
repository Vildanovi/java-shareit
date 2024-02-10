package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import javax.validation.constraints.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDto {
    private int id;
    @NotBlank(groups = {CreatedBy.class}, message = "Вещь не может быть без названия")
    @Size(groups = {CreatedBy.class}, max = 255, message = "Имя > 255 символов")
    private String name; // краткое название
    @Size(groups = {CreatedBy.class, LastModifiedBy.class}, max = 500, message = "Описание > 500 символов")
    @NotBlank(groups = {CreatedBy.class}, message = "Вещь не может быть без описания")
    private String description; // развёрнутое описание
    @NotNull(groups = {CreatedBy.class}, message = "Вещь не может быть статуса доступности")
    private Boolean available; // статус о том, доступна или нет вещь для аренды
    private int requestId;
}
