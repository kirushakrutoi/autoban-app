package ru.kirill.portalService.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kirill.portalService.mappers.Mapper;
import ru.kirill.portalService.model.DTOs.AdataDto;
import ru.kirill.portalService.model.DTOs.CompanyDTO;
import ru.kirill.portalService.model.DTOs.RegisterDTO;
import ru.kirill.portalService.model.User;
import ru.kirill.portalService.services.AdataService;
import ru.kirill.portalService.services.KeycloakService;

@RestController
@RequestMapping("")
public class RegisterController {
    @Autowired
    private KeycloakService keycloakService;
    @Autowired
    private AdataService adataService;

    @PostMapping("/register")
    public ResponseEntity<HttpStatus> createRegister(@RequestBody RegisterDTO registerDTO) {
        return keycloakService.createRegister(registerDTO);

    }

    @PostMapping("/register/create/company")
    public ResponseEntity<HttpStatus> query(@RequestBody AdataDto adataDto, @RequestHeader HttpHeaders headers) throws JsonProcessingException {
        return keycloakService.createCompany(adataDto, Mapper.getUserFromHeaders(headers));
    }



}
