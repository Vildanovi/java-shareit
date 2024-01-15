package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import javax.validation.constraints.NotBlank;
import java.time.Instant;

@Data
@Builder
public class CommentDto {

    private int id;
    @NotBlank(groups = {CreatedBy.class}, message = "Комментарий не может быть пустым")
    private String text;
    private Item item;
    private User author;
    private Instant created;
}
