package ru.practicum.shareit.booking.exception;

public class ValidationBadRequestException extends RuntimeException {

    public ValidationBadRequestException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
