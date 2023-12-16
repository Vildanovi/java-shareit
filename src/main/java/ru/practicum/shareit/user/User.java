package ru.practicum.shareit.user;

import lombok.*;
import org.springframework.stereotype.Service;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * TODO Sprint add-controllers.
 */

@Data
@Builder
public class User {
    private int id;
    @Size(max = 20)
    @Pattern(regexp = "[\\S]{0,}", message = "Имя не должно содержать пробелы")
    private String name; // имя или логин пользователя
    @NotBlank(message = "email не может быть пустым")
    @Email(message = "Некорректный email")
    private String email;
}
