package ru.kirill.dwhService.exceptions;

public class ForbiddenException extends Exception{
    public ForbiddenException(String message) {
        super(message);
    }
}
