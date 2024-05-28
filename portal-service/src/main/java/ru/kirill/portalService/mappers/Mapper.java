package ru.kirill.portalService.mappers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import ru.kirill.portalService.model.DTOs.CompanyDTO;
import ru.kirill.portalService.model.DTOs.RegisterDTO;
import ru.kirill.portalService.model.User;
import ru.kirill.portalService.services.GeneratePasswordService;

import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
public class Mapper {

    public static User getUserFromHeaders(HttpHeaders headers) throws JsonProcessingException {
        User user = new User();
        user.setUserId(headers.get("userid").get(0));
        user.setUsername(headers.get("username").get(0));
        user.setClientRoles(getClientRoles(headers));
        return user;
    }

    private static Map<String, String> getClientRoles(HttpHeaders headers) throws JsonProcessingException {
        String clientRolesHeader = headers.get("clientroles").get(0);
        String encodeHeader = new String(Base64.getDecoder().decode(clientRolesHeader));
        return new ObjectMapper().readerFor(Map.class).readValue(encodeHeader);
    }

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
