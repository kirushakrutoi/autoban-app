package ru.kirill.portalService.exceptions.userexception;

public class ForbiddenException extends Exception{
    public ForbiddenException(String message) {
        super(message);
    }
}
