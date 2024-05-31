package ru.kirill.portalService.exceptions;

public class CarAlreadyExistException extends Exception{
    public CarAlreadyExistException(String message) {
        super(message);
    }
}
