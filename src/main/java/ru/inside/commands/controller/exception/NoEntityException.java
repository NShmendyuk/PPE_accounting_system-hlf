package ru.inside.commands.controller.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@RequiredArgsConstructor
@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Entity not found.")
public class NoEntityException extends Exception {
    private static final long serialVersionUID = 6523584358007803996L;
    private final String entity;
    private final Long uid;
    private final String param;

    /**
     * Конструктор ошибки отсутсвия сущности
     * @param entity тип сущности
     * @param uid запрашиваемый уникальный номер сущности
     * @return ошибка по сущности и номеру
     */
    public static NoEntityException createWithId(String entity, Long uid) {
        return new NoEntityException(entity, uid, null);
    }

    /**
     * Конструктор ошибки отсутсвия сущности
     * @param entity тип сущности
     * @param param запрашиваемый параметр
     * @return ошибка по сущности и номеру
     */
    public static NoEntityException createWithParam(String entity, String param) {
        return new NoEntityException(entity, null, param);
    }

    public String getMessage() {
        if (uid != null)
            return "The server reported: " + entity + " with ID=" + uid + " was not found.";
        else if (param != null)
            return "The server reported: " + entity + " with param=" + param + " was not found.";
        else
            return "The server reported: " + entity + "was not found.";
    }
}