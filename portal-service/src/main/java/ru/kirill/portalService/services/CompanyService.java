package ru.kirill.portalService.services;

import jakarta.ws.rs.core.Response;
import lombok.Data;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.kirill.portalService.mappers.Mapper;
import ru.kirill.portalService.model.DTOs.AdataDto;
import ru.kirill.portalService.model.DTOs.CompanyDTO;
import ru.kirill.portalService.model.DTOs.UserDTO;
import ru.kirill.portalService.model.User;

import java.util.Collections;

@Service
@Data
public class CompanyService {
    private KeycloakService keycloakService;
    private final Keycloak keycloak;
    private final RealmResource realm;
    @Value("${keycloak.realm}")
    private String KEYCLOAK_REALM;
    private String ROLE_REGISTER = "REGISTER";
    private MailSenderService mailSenderService;
    @Autowired
    private AdataService adataService;

    @Autowired
    public CompanyService(Keycloak keycloak, RealmResource realm, MailSenderService mailSenderService, AdataService adataService, KeycloakService keycloakService) {
        this.keycloak = keycloak;
        this.realm = realm;
        this.mailSenderService = mailSenderService;
        this.keycloakService = keycloakService;
    }

    public ResponseEntity<HttpStatus> createCompany(AdataDto adataDto, User user){
        CompanyDTO companyDTO = adataService.getInfoByInn(adataDto);
        ClientRepresentation clientRepresentation = Mapper.convertToClientRepresentation(companyDTO);
        return keycloakService.createCompany(clientRepresentation, user);
    }


}
