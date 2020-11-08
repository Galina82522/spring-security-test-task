package ru.codeinside.springsecuritytesttask.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class NoteNotFoundException extends RuntimeException {
    public NoteNotFoundException(String message) {
        super(message);
    }
}