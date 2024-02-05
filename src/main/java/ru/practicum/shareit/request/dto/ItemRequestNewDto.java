package ru.practicum.shareit.request.dto;

import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ItemRequestNewDto {
    @NotBlank(groups = {CreatedBy.class}, message = "Запрос не может быть без описания")
    @Size(groups = {CreatedBy.class}, max = 255, message = "Имя > 255 символов")
    private String description;
}
