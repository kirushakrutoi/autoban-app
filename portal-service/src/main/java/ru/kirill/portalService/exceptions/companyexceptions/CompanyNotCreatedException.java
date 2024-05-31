package ru.kirill.portalService.exceptions.companyexceptions;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class CompanyNotCreatedException extends Exception{
    private HttpStatus status;

    public CompanyNotCreatedException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
