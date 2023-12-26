package ru.practicum.shareit.booking.enumerations;

public enum BookingStatus {
    WAITING, // Новое бронирование, ожидает одобрения
    APPROVED, // Бронирование подтверждено владельцем
    REJECTED, // бронирование отклонено владельцем
    CANCELED // Бронирование отменено создателем
}
