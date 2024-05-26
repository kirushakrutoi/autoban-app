package ru.kirill.portalService.services;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import lombok.Data;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.kirill.portalService.mappers.Mapper;
import ru.kirill.portalService.model.DTOs.RegisterDTO;

import java.net.URI;
import java.util.Collections;

@Service
@Data
public class KeycloakService {
    private final Keycloak keycloak;
    private final RealmResource realm;
    @Value("${keycloak.realm}")
    private String KEYCLOAK_REALM;
    private String ROLE_REGISTER = "REGISTER";
    private MailSenderService mailSenderService;

    @Autowired
    public KeycloakService(Keycloak keycloak, RealmResource realm, MailSenderService mailSenderService) {
        this.keycloak = keycloak;
        this.realm = realm;
        this.mailSenderService = mailSenderService;
    }


    public ResponseEntity<HttpStatus> createRegister(RegisterDTO registerDTO){
        UserRepresentation userRepresentation = Mapper.convertToUserRepresentation(registerDTO);

        Response result = realm.users().create(userRepresentation);

        setRole(result, ROLE_REGISTER);
        sendPassword(userRepresentation);

        return new ResponseEntity<>(HttpStatus.valueOf(result.getStatus()));
    }

    private void sendPassword(UserRepresentation userRepresentation) {
        CredentialRepresentation credential = userRepresentation.getCredentials().get(0);

        String password = credential.getValue();

        mailSenderService.sendSimpleMessage(userRepresentation.getEmail(), "Password from keycloak", password);
    }

    private String getCreatedId(Response response) {
        URI location = response.getLocation();

        if (!response.getStatusInfo().equals(Response.Status.CREATED)) {
            Response.StatusType statusInfo = response.getStatusInfo();
            throw new WebApplicationException("Create method returned status " +
                    statusInfo.getReasonPhrase() + " (Code: " + statusInfo.getStatusCode() + "); expected status: Created (201)", response);
        }

        if (location == null) {
            return null;
        }

        String path = location.getPath();
        return path.substring(path.lastIndexOf('/') + 1);
    }

    private void setRole(Response response, String role){
        RoleResource roleResource = realm.roles().get(role);
        UserResource userResource = realm.users().get(getCreatedId(response));

        RoleRepresentation rolesRepresentation = roleResource.toRepresentation();

        userResource.roles().realmLevel().add(Collections.singletonList(rolesRepresentation));
    }
}
