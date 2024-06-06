package ru.kirill.logistService.exceptions;

public class RouteNotFoundException extends Exception{
    public RouteNotFoundException(String message) {
        super(message);
    }
}
