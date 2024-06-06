package ru.kirill.logistService.exceptions;

public class IncorrectStatusException extends Exception{
    public IncorrectStatusException(String message) {
        super(message);
    }
}
