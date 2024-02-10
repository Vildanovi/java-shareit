package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestForRequestDto {
    @NotBlank(groups = {CreatedBy.class}, message = "Запрос не может быть без описания")
    @Size(groups = {CreatedBy.class}, max = 255, message = "Имя > 255 символов")
    private String description;
}
