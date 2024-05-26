package ru.kirill.portalService.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kirill.portalService.model.DTOs.RegisterDTO;
import ru.kirill.portalService.services.KeycloakService;

@RestController
@RequestMapping("")
public class RegisterController {
    @Autowired
    private KeycloakService keycloakService;

    @PostMapping("/register")
    public ResponseEntity<HttpStatus> createRegister(@RequestBody RegisterDTO registerDTO) {
        return keycloakService.createRegister(registerDTO);

    }
}
