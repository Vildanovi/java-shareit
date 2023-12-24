package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponseDto {

    private int id;
    private String name; // имя или логин пользователя
    private String email;
}
