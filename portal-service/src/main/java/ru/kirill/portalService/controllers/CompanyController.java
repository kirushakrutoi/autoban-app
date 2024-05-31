package ru.kirill.portalService.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.kirill.portalService.exceptions.companyexceptions.CompanyNotCreatedException;
import ru.kirill.portalService.exceptions.companyexceptions.CompanyNotFoundException;
import ru.kirill.portalService.exceptions.companyexceptions.InvalidInnException;
import ru.kirill.portalService.exceptions.userexception.UserNotFoundException;
import ru.kirill.portalService.mappers.Mapper;
import ru.kirill.portalService.model.DTOs.*;
import ru.kirill.portalService.services.AdataService;
import ru.kirill.portalService.services.CompanyService;
import ru.kirill.portalService.services.KeycloakService;

import java.util.List;

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
    public ResponseEntity<String> query(@RequestBody @Valid AdataDto adataDto,
                                        @RequestHeader HttpHeaders headers,
                                        BindingResult bindingResult) throws JsonProcessingException {
        if(bindingResult.hasErrors()){
            return new ResponseEntity<>(getErrorMessage(bindingResult), HttpStatus.BAD_REQUEST);
        }

        try {
            companyService.createCompany(adataDto, Mapper.getUserFromHeaders(headers));
            return new ResponseEntity<>("The company has been successfully added", HttpStatus.CREATED);
        } catch (CompanyNotCreatedException e) {
            return new ResponseEntity<>(e.getMessage(), e.getStatus());
        } catch (InvalidInnException | UserNotFoundException | CompanyNotFoundException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/get")
    public ResponseEntity<?> getCompany(@RequestBody GetCompanyDTO getCompanyDTO){
        try {
            return new ResponseEntity<>(companyService.getCompany(getCompanyDTO), HttpStatus.OK);
        } catch (CompanyNotFoundException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping("/get/all/paging")
    public ResponseEntity<List<MinCompanyDTO>> getCompanies(@RequestBody GetPagingDTO pagingDTO){
        return new ResponseEntity<>(companyService.getCompanies(pagingDTO), HttpStatus.OK);
    }

    private String getErrorMessage(BindingResult bindingResult){
        StringBuilder stringBuilder = new StringBuilder();

        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            stringBuilder.append(fieldError.getField())
                    .append(" - ")
                    .append(fieldError.getDefaultMessage())
                    .append("; ");
        }

        return stringBuilder.toString();
    }
}
