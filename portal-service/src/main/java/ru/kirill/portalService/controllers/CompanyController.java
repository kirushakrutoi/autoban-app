package ru.kirill.portalService.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
    public ResponseEntity<HttpStatus> query(@RequestBody AdataDto adataDto,
                                            @RequestHeader HttpHeaders headers) throws JsonProcessingException {
        return companyService.createCompany(adataDto, Mapper.getUserFromHeaders(headers));
    }

    @GetMapping("/get")
    public ResponseEntity<FullCompanyDTO> getCompany(@RequestBody GetCompanyDTO getCompanyDTO){
        return new ResponseEntity<>(companyService.getCompany(getCompanyDTO), HttpStatus.OK);
    }

    @GetMapping("/get/all/paging")
    public ResponseEntity<List<MinCompanyDTO>> getCompanies(@RequestBody GetPagingDTO pagingDTO){
        return new ResponseEntity<>(companyService.getCompanies(pagingDTO), HttpStatus.OK);
    }
}
