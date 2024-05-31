package ru.kirill.portalService.services;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import lombok.Data;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.kirill.portalService.mappers.Mapper;
import ru.kirill.portalService.model.DTOs.AdataDto;
import ru.kirill.portalService.model.DTOs.CompanyDTO;
import ru.kirill.portalService.model.DTOs.RegisterDTO;
import ru.kirill.portalService.model.DTOs.UserDTO;
import ru.kirill.portalService.model.User;

import java.net.URI;
import java.util.*;

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
    private AdataService adataService;

    @Autowired
    public KeycloakService(Keycloak keycloak, RealmResource realm, MailSenderService mailSenderService, AdataService adataService) {
        this.keycloak = keycloak;
        this.realm = realm;
        this.mailSenderService = mailSenderService;
    }


    public ResponseEntity<HttpStatus> addUser(UserRepresentation userRepresentation){
        Response result = realm.users().create(userRepresentation);

        setRole(result, ROLE_REGISTER);
        sendPassword(userRepresentation);

        return new ResponseEntity<>(HttpStatus.valueOf(result.getStatus()));
    }

    public ResponseEntity<HttpStatus> createCompany(ClientRepresentation clientRepresentation, User user){
        Response response = realm.clients().create(clientRepresentation);

        ClientResource clientResource = realm.clients().get(getCreatedClientId(response));
        createClientRoles(clientResource);

        addClientRole(user.getUserId(), clientResource, getCreatedClientId(response), "ADMIN");

        return new ResponseEntity<>(HttpStatus.valueOf(response.getStatus()));
    }

    public UserRepresentation getUserRepresentationByUsername(String username){
        return realm.users().searchByUsername(username, true).get(0);
    }

    public UserResource getUserResource(String id){
        realm.users().searchByAttributes("company");
        return realm.users().get(id);
    }

    public ClientResource getClientResourceById(String clientId){
        ClientRepresentation clientRepresentation = realm.clients().findByClientId(clientId).get(0);
        return realm.clients().get(clientRepresentation.getId());
    }

    public String getUserIdByUserName(String username){
        UserRepresentation userRepresentation = realm.users().searchByUsername(username, true).get(0);
        return userRepresentation.getId();
    }

    public String getClientIdByName(String companyName){
        return realm.clients().findByClientId(companyName).get(0).getId();
    }

    public void addClientRole(String userId, ClientResource clientResource, String clientId,String role){
        UserResource userResource = realm.users().get(userId);
        RoleResource roleResource = clientResource.roles().get(role);
        RoleRepresentation roleRepresentation = roleResource.toRepresentation();
        userResource.roles().clientLevel(clientId).add(Collections.singletonList(roleRepresentation));
    }

    public void deleteClientRole(String userId, String clientID) {
        UserResource userResource = realm.users().get(userId);
        List<RoleRepresentation> roles = Optional.ofNullable(userResource.roles().clientLevel(clientID).listAll()).orElse(new ArrayList<>());
        if(roles.isEmpty())
            return;
        userResource.roles().clientLevel(clientID).remove(roles);
    }

    public List<UserRepresentation> getUserHasClientRole(String clientId){
        return realm.users().searchByAttributes(clientId);
    }

    public List<ClientRepresentation> getAllClients(){
        return realm.clients().findAll();
    }

    private void sendPassword(UserRepresentation userRepresentation) {
        CredentialRepresentation credential = userRepresentation.getCredentials().get(0);

        String password = credential.getValue();

        mailSenderService.sendSimpleMessage(userRepresentation.getEmail(), "Password from keycloak", password);
    }

    private String getCreatedClientId(Response response){
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

    private String getCreatedUserId(Response response) {
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

    private void createClientRoles(ClientResource clientResource){
        RoleRepresentation adminRole = new RoleRepresentation();
        adminRole.setName("ADMIN");
        RoleRepresentation driverRole = new RoleRepresentation();
        driverRole.setName("DRIVER");
        RoleRepresentation logistRole = new RoleRepresentation();
        logistRole.setName("LOGIST");

        clientResource.roles().create(adminRole);
        clientResource.roles().create(driverRole);
        clientResource.roles().create(logistRole);
    }

    private void setRole(Response response, String role){
        RoleResource roleResource = realm.roles().get(role);
        UserResource userResource = realm.users().get(getCreatedUserId(response));

        RoleRepresentation rolesRepresentation = roleResource.toRepresentation();

        userResource.roles().realmLevel().add(Collections.singletonList(rolesRepresentation));
    }

}
