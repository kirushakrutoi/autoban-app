package ru.kirill.portalService.exceptions.companyexceptions;

public class CompanyNotFoundException extends Exception{
    public CompanyNotFoundException(String message) {
        super(message);
    }
}
