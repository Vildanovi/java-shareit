package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommentRequestDto {
    @NotBlank(groups = {CreatedBy.class}, message = "Комментарий не может быть пустым")
    @Size(groups = {CreatedBy.class}, max = 255, message = "Комментарий > 255 символов")
    private String text;
}
