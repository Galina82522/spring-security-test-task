package ru.codeinside.springsecuritytesttask.exceptions;

public class NotAllowedException extends RuntimeException {
    public NotAllowedException() {
        super("Операция не позволена");
    }
}