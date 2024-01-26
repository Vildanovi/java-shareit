package ru.practicum.shareit.request.dto;

import lombok.*;
import org.springframework.data.annotation.CreatedBy;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ItemRequestNewDto {
    @NotBlank(groups = {CreatedBy.class}, message = "Запрос не может быть без описания")
    private String description;
}
