package ru.practicum.shareit.user.model;

import lombok.*;

@AllArgsConstructor
@Builder
@Getter
@Setter
public class User {

    private int id;
    private String name; // имя или логин пользователя
    private String email;
}
