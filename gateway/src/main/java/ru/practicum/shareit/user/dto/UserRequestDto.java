package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDto {

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
