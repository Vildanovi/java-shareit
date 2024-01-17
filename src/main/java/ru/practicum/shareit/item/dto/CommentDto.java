package ru.practicum.shareit.item.dto;

import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class CommentDto {
    @NotBlank(groups = {CreatedBy.class}, message = "Комментарий не может быть пустым")
    @Size(groups = {CreatedBy.class}, max = 255, message = "Комментарий > 255 символов")
    private String text;
}
