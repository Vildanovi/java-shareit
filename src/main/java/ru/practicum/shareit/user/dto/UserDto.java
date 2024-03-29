package ru.practicum.shareit.user.dto;

import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import javax.validation.constraints.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class UserDto {

    private int id;
    @Size(groups = {CreatedBy.class, LastModifiedBy.class}, max = 20,
            message = "Имя пользователя > 20 символов")
    @Pattern(groups = {CreatedBy.class, LastModifiedBy.class}, regexp = "[\\S]{0,}",
            message = "Имя не должно содержать пробелы")
    @NotEmpty(groups = {CreatedBy.class},
            message = "Имя не должно быть пустым")
    private String name; // имя или логин пользователя
    @NotEmpty(groups = {CreatedBy.class},
            message = "Email не может быть пустым")
    @Email(groups = {CreatedBy.class, LastModifiedBy.class},
            message = "Некорректный email")
    private String email;
}
