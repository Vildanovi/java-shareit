package ru.practicum.shareit.user.dto;

import lombok.*;
import org.hibernate.sql.Update;
import org.springframework.data.annotation.CreatedBy;
import javax.validation.constraints.*;

@AllArgsConstructor
@Builder
@Getter
@Setter
public class UserDto {

    private int id;
    @Size(groups = {CreatedBy.class}, max = 20)
    @Pattern(regexp = "[\\S]{0,}", message = "Имя не должно содержать пробелы")
    @NotEmpty(groups = {CreatedBy.class})
    private String name; // имя или логин пользователя
    @NotEmpty(groups = {CreatedBy.class}, message = "email не может быть пустым")
    @Email(groups = {CreatedBy.class, Update.class}, message = "Некорректный email")
    private String email;
}
