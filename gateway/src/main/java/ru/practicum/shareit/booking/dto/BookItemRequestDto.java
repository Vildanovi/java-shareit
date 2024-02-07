package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookItemRequestDto {
    @NotNull(groups = {CreatedBy.class}, message = "Бронирование не может быть без идентификатора вещи")
    private Integer itemId;
    @NotNull(groups = {CreatedBy.class}, message = "Бронирование не может быть без даты начала")
    @FutureOrPresent(groups = {CreatedBy.class}, message = "Дата начала бронирования не может быть в прошлом")
    private LocalDateTime start;
    @NotNull(groups = {CreatedBy.class}, message = "Бронирование не может быть без даты конца")
    @Future(groups = {CreatedBy.class}, message = "Дата завершения бронирования не может быть в прошлом")
    private LocalDateTime end;
}