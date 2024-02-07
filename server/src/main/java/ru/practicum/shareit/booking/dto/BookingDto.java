package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.enumerations.BookingStatus;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class BookingDto {
    private int id;
    private LocalDateTime start; // дата и время начала бронирования
    private LocalDateTime end; // дата и время конца бронирования
    private int itemId; // вещь, которую пользователь бронирует
    private int bookerId; // пользователь, который осуществляет бронирование
    private BookingStatus status; // статус бронирования
}
