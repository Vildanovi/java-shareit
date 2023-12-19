package ru.practicum.shareit.exception;

public class EntityUpdateException extends RuntimeException {

    public EntityUpdateException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
