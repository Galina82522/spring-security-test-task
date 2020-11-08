package ru.codeinside.springsecuritytesttask.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.codeinside.springsecuritytesttask.controllers.dto.ErrorDTO;
import ru.codeinside.springsecuritytesttask.exceptions.NotAllowedException;
import ru.codeinside.springsecuritytesttask.exceptions.NoteNotFoundException;
import ru.codeinside.springsecuritytesttask.exceptions.UserAlreadyExistException;
import ru.codeinside.springsecuritytesttask.exceptions.UserNotFoundException;


@ControllerAdvice
public class ErrorControllerAdvice {

    @ExceptionHandler(NotAllowedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ResponseBody
    public ErrorDTO notAllowedExceptionHandler(Exception exception) {
        return new ErrorDTO(HttpStatus.METHOD_NOT_ALLOWED, exception.getLocalizedMessage());
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ErrorDTO userAlreadyExistHandler(Exception exception) {
        return new ErrorDTO(HttpStatus.CONFLICT, exception.getLocalizedMessage());
    }

    @ExceptionHandler(value = {UserNotFoundException.class, NoteNotFoundException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorDTO badRequestHandler(Exception exception) {
        return new ErrorDTO(HttpStatus.BAD_REQUEST, exception.getLocalizedMessage());
    }

    @ExceptionHandler(value = {AccessDeniedException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public ErrorDTO accessDeniedHandler(Exception exception) {
        return new ErrorDTO(HttpStatus.FORBIDDEN, exception.getLocalizedMessage());
    }

}
