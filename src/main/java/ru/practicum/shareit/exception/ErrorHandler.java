package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus
    public ErrorResponse handlerValidationException(final ValidationBadRequestException exception) {
        log.debug("Ошибка валидации со статусом 400 {}", exception.getMessage());
        return new ErrorResponse(exception.getMessage());
    }
}
