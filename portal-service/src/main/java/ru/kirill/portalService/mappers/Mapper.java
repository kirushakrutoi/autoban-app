package ru.kirill.portalService.mappers;

import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Component;
import ru.kirill.portalService.model.DTOs.RegisterDTO;
import ru.kirill.portalService.services.GeneratePasswordService;

import java.util.Collections;

@Component
public class Mapper {
    public static UserRepresentation convertToUserRepresentation(RegisterDTO registerDTO){
        UserRepresentation userRepresentation = new UserRepresentation();

        userRepresentation.setEnabled(true);
        userRepresentation.setEmail(registerDTO.getEmail());
        userRepresentation.setEmailVerified(true);
        userRepresentation.setUsername(registerDTO.getUsername());
        userRepresentation.setFirstName(registerDTO.getFirstName());
        userRepresentation.setLastName(registerDTO.getLastName());

        String password = GeneratePasswordService.generatePassword();
        userRepresentation.setCredentials(Collections.singletonList(createCredential(password)));

        return userRepresentation;
    }

    private static CredentialRepresentation createCredential(String password){
        CredentialRepresentation credentials = new CredentialRepresentation();

        credentials.setType(CredentialRepresentation.PASSWORD);
        credentials.setValue(password);
        credentials.setTemporary(false);

        return credentials;
    }
}
