package ru.inside.commands.controller.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalControllerExceptionHandler {
    @ExceptionHandler(NoEntityException.class)
    protected ResponseEntity<Object> handleException(NoEntityException e) {
        ResponseException response = new ResponseException(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestBodyException.class)
    protected ResponseEntity<Object> handleException(BadRequestBodyException e) {
        ResponseException response = new ResponseException(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadRequestBodyException.class)
    protected ResponseEntity<Object> handleException(EntityCollisionException e) {
        ResponseException response = new ResponseException(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }
}