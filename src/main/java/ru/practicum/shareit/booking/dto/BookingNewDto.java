package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingNewDto {
    private int ItemId;
    private LocalDateTime start;
    private LocalDateTime end;
}
