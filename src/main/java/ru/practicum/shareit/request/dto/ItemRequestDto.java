package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */

@Data
@Builder
public class ItemRequestDto {
    private int id;
    private String description; // текст запроса, содержащий описание требуемой вещи;
    private int requestor; // пользователь, создавший запрос
    private LocalDateTime created; // дата и время создания запроса
}
