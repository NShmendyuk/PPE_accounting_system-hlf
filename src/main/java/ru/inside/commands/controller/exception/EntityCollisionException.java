package ru.inside.commands.controller.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@RequiredArgsConstructor
@ResponseStatus(code = HttpStatus.CONFLICT, reason = "Entity Collision Found.")
public class EntityCollisionException extends Exception {
    private static final long serialVersionUID = 428852231140560807L;
    private final String entity;
    private final String param;
    private final String collisionReason;

    /**
     * Конструктор ошибки коллизий сущностей
     * @param entity тип сущности
     * @return ошибка по сущности и параметру
     */
    public static EntityCollisionException createWith(String entity, String param, String collisionReason) {
        return new EntityCollisionException(entity, param, collisionReason);
    }

    public String getMessage() {
        return "The server reported! Cannot create " + entity + " with param: " + param + ". collision reason: " + collisionReason;
    }
}