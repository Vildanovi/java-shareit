package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.enumerations.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class BookingResponseDto {
    private int id;
    private LocalDateTime start; // дата и время начала бронирования
    private LocalDateTime end; // дата и время конца бронирования
    private Item item; // вещь, которую пользователь бронирует
    private User booker; // пользователь, который осуществляет бронирование
    private BookingStatus status; // статус бронирования
}