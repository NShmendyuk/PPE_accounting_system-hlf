package ru.inside.commands.controller.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@RequiredArgsConstructor
@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Empty Request Body.")
public class BadRequestBodyException extends Exception {
    private static final long serialVersionUID = -6219107521443488738L;
    private final String entity;

    /**
     * Конструктор ошибки пустого тела запроса
     * @param entity тип сущности
     * @return ошибка по сущности и номеру
     */
    public static BadRequestBodyException createWith(String entity) {
        return new BadRequestBodyException(entity);
    }

    public String getMessage() {
        return "The server reported: An empty request body was received! Cannot create " + entity;
    }
}