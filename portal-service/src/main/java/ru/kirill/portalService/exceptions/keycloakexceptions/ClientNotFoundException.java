package ru.kirill.portalService.exceptions.keycloakexceptions;

public class ClientNotFoundException extends Exception{
    public ClientNotFoundException(String message) {
        super(message);
    }
}
