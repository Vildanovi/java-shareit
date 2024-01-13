package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import ru.practicum.shareit.booking.enumerations.BookingStatus;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */

@Data
@Builder
public class BookingDto {
    @NotNull(message = "Бронирование не может быть без идентификатора")
    private int id;
    @NotNull(groups = {CreatedBy.class}, message = "Бронирование не может быть без даты начала")
    @FutureOrPresent
    private LocalDateTime start; // дата и время начала бронирования
    @NotNull(groups = {CreatedBy.class}, message = "Бронирование не может быть без даты конца")
    @Future
    private LocalDateTime end; // дата и время конца бронирования
    private int itemId; // вещь, которую пользователь бронирует
    private int bookerId; // пользователь, который осуществляет бронирование
    private BookingStatus status; // статус бронирования
}
