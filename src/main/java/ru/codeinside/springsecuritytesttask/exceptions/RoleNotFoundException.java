package ru.codeinside.springsecuritytesttask.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class RoleNotFoundException extends RuntimeException {
    public RoleNotFoundException(String message) {
        super(message);
    }
}