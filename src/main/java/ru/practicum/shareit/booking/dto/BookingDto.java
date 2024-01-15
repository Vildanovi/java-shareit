package ru.practicum.shareit.booking.dto;

import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import ru.practicum.shareit.booking.enumerations.BookingStatus;
import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class BookingDto {
    private int id;
    @NotNull(groups = {CreatedBy.class}, message = "Бронирование не может быть без даты начала")
    @FutureOrPresent(groups = {CreatedBy.class}, message = "Дата начала бронирования не может быть в прошлом")
    private LocalDateTime start; // дата и время начала бронирования
    @NotNull(groups = {CreatedBy.class}, message = "Бронирование не может быть без даты конца")
    @Future(groups = {CreatedBy.class}, message = "Дата завершения бронирования не может быть в прошлом")
    private LocalDateTime end; // дата и время конца бронирования
    private int itemId; // вещь, которую пользователь бронирует
    private int bookerId; // пользователь, который осуществляет бронирование
    private BookingStatus status; // статус бронирования
}
