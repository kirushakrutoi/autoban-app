package ru.kirill.portalService.mappers;

import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Component;
import ru.kirill.portalService.model.DTOs.CompanyDTO;
import ru.kirill.portalService.model.DTOs.RegisterDTO;
import ru.kirill.portalService.services.GeneratePasswordService;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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

    public static ClientRepresentation convertToClientRepresentation(CompanyDTO companyDTO){
        ClientRepresentation clientRepresentation = new ClientRepresentation();

        clientRepresentation.setEnabled(true);
        clientRepresentation.setClientId(companyDTO.getName());
        clientRepresentation.setAttributes(createAttributes(companyDTO));
        return clientRepresentation;
    }

    private static Map<String, String> createAttributes(CompanyDTO companyDTO){
        Map<String, String> attributes = new HashMap<>();
        attributes.put("inn", companyDTO.getInn());
        attributes.put("kpp", companyDTO.getKpp());
        attributes.put("ogrn", companyDTO.getOgrn());
        attributes.put("address", companyDTO.getAddress());
        return attributes;
    }

    private static CredentialRepresentation createCredential(String password){
        CredentialRepresentation credentials = new CredentialRepresentation();

        credentials.setType(CredentialRepresentation.PASSWORD);
        credentials.setValue(password);
        credentials.setTemporary(false);

        return credentials;
    }
}
