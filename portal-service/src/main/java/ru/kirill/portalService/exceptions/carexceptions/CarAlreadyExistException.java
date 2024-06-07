package ru.kirill.portalService.exceptions.carexceptions;

public class CarAlreadyExistException extends Exception{
    public CarAlreadyExistException(String message) {
        super(message);
    }
}
