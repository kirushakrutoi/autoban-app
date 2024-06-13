package ru.kirill.dwhService.exceptions;

public class CompanyNotFoundException extends Exception{
    public CompanyNotFoundException(String message) {
        super(message);
    }
}
