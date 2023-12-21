package ru.practicum.shareit.user.model;

import lombok.*;
import javax.validation.constraints.*;

/**
 * TODO Sprint add-controllers.
 */

@AllArgsConstructor
@Builder
@Getter
@Setter
public class User {
    private int id;
    private String name; // имя или логин пользователя
    private String email;
}
