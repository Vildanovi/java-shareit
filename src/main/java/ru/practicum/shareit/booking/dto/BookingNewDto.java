package ru.practicum.shareit.booking.dto;

import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import javax.validation.constraints.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class BookingNewDto {
    @NotNull(groups = {CreatedBy.class}, message = "Бронирование не может быть без идентификатора вещи")
    private Integer itemId;
    @NotNull(groups = {CreatedBy.class}, message = "Бронирование не может быть без даты начала")
    @FutureOrPresent(groups = {CreatedBy.class}, message = "Дата начала бронирования не может быть в прошлом")
    private LocalDateTime start;
    @NotNull(groups = {CreatedBy.class}, message = "Бронирование не может быть без даты конца")
    @Future(groups = {CreatedBy.class}, message = "Дата завершения бронирования не может быть в прошлом")
    private LocalDateTime end;
}
