package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDto;

@Data
@Builder
public class ItemResponseDto {

    private int id;
    private String name; // краткое название
    private String description; // развёрнутое описание
    private Boolean available; // статус о том, доступна или нет вещь для аренд
    private BookingDto lastBooking;
    private BookingDto nextBooking;
    private int requestId;
}
