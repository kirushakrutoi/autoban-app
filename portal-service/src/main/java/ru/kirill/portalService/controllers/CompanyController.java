package ru.kirill.portalService.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kirill.portalService.mappers.Mapper;
import ru.kirill.portalService.model.DTOs.AdataDto;
import ru.kirill.portalService.model.DTOs.UserDTO;
import ru.kirill.portalService.services.AdataService;
import ru.kirill.portalService.services.CompanyService;
import ru.kirill.portalService.services.KeycloakService;

@RestController
@RequestMapping("/company")
public class CompanyController {
    @Autowired
    private CompanyService companyService;
    @Autowired
    private KeycloakService keycloakService;
    @Autowired
    private AdataService adataService;

    @PostMapping("/create")
    public ResponseEntity<HttpStatus> query(@RequestBody AdataDto adataDto,
                                            @RequestHeader HttpHeaders headers) throws JsonProcessingException {
        return companyService.createCompany(adataDto, Mapper.getUserFromHeaders(headers));
    }

/*    @PostMapping("/add/user")
    public ResponseEntity<HttpStatus> addUser(@RequestBody UserDTO userDTO,
                                              @RequestHeader HttpHeaders headers) throws JsonProcessingException {
        companyService.addClientRoleForExistUser(userDTO, Mapper.getUserFromHeaders(headers));
        System.out.println("asdf");
        return new ResponseEntity<>(HttpStatus.OK);
    }*/
}
