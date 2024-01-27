package ru.practicum.shareit.request.dto;

import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ItemRequestNewDto {
    @NotBlank(groups = {CreatedBy.class}, message = "Запрос не может быть без описания")
    private String description;
}
