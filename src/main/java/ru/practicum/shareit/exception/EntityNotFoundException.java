package ru.practicum.shareit.exception;

public class EntityNotFoundException extends RuntimeException{

    public EntityNotFoundException (String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
