package ru.kirill.portalService.mappers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import ru.kirill.portalService.model.DTOs.*;
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

    public static FullCompanyDTO getCompanyFromRepresentation(ClientRepresentation clientRepresentation, long countDriver, long countLogist){
        FullCompanyDTO companyDTO = new FullCompanyDTO();
        Map<String, String> attributes = clientRepresentation.getAttributes();
        companyDTO.setAddress(attributes.get("address"));
        companyDTO.setName(clientRepresentation.getClientId());
        companyDTO.setKpp(attributes.get("kpp"));
        companyDTO.setInn(attributes.get("inn"));
        companyDTO.setOgrn(attributes.get("ogrn"));
        companyDTO.setId(clientRepresentation.getId());
        companyDTO.setCountDriver(countDriver);
        companyDTO.setCountLogist(countLogist);
        return companyDTO;
    }

    public static MinCompanyDTO getCompanyFromRepresentation(ClientRepresentation clientRepresentation){
        MinCompanyDTO minCompanyDTO = new MinCompanyDTO();
        minCompanyDTO.setId(clientRepresentation.getId());
        minCompanyDTO.setName(clientRepresentation.getClientId());
        minCompanyDTO.setInn(clientRepresentation.getAttributes().get("inn"));
        return minCompanyDTO;
    }

    public static UserDTO getUserDTOFromRepresentation(UserRepresentation userRepresentation, String companyName){
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(userRepresentation.getUsername());
        userDTO.setCompanyName(companyName);
        userDTO.setCompanyRole(userRepresentation.firstAttribute(companyName));
        return userDTO;
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
