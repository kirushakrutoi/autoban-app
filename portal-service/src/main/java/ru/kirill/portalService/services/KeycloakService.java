package ru.kirill.portalService.services;

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
import ru.kirill.portalService.exceptions.keycloakexceptions.ClientNotFoundException;
import ru.kirill.portalService.exceptions.keycloakexceptions.KeycloakException;
import ru.kirill.portalService.exceptions.userexception.UserNotFoundException;
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


    public void addUser(UserRepresentation userRepresentation) throws KeycloakException {
        Response result = realm.users().create(userRepresentation);

        setRole(result, ROLE_REGISTER);

        sendPassword(userRepresentation);
    }

    public void createCompany(ClientRepresentation clientRepresentation, User user) throws KeycloakException {
        Response response = realm.clients().create(clientRepresentation);

        ClientResource clientResource = realm.clients().get(getCreatedId(response));
        createClientRoles(clientResource);

        addClientRole(user.getUserId(), clientResource, getCreatedId(response), "ADMIN");
    }

    public UserRepresentation getUserRepresentationByUsername(String username){
        return realm.users().searchByUsername(username, true).get(0);
    }

    public UserResource getUserResource(String id) throws UserNotFoundException {
        try {
            return realm.users().get(id);
        } catch (Exception e){
            throw new UserNotFoundException("Invalid id");
        }

    }

    public ClientResource getClientResourceById(String clientId) throws ClientNotFoundException {
        try {
            ClientRepresentation clientRepresentation = realm.clients().findByClientId(clientId).get(0);
            return realm.clients().get(clientRepresentation.getId());
        } catch (Exception e){
            throw new ClientNotFoundException("Invalid company name");
        }

    }

    public String getUserIdByUserName(String username) throws UserNotFoundException {
        try {
            UserRepresentation userRepresentation = realm.users().searchByUsername(username, true).get(0);
            return userRepresentation.getId();
        } catch (Exception e){
            throw new UserNotFoundException("Invalid username");
        }
    }

    public String getClientIdByName(String companyName) throws ClientNotFoundException {
        try {
            return realm.clients().findByClientId(companyName).get(0).getId();
        } catch (Exception e){
            throw new ClientNotFoundException("");
        }
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

    public List<UserRepresentation> getAllUSer(){
        return realm.users().list();
    }

    public List<ClientRepresentation> getAllClients(){
        return realm.clients().findAll();
    }

    private void sendPassword(UserRepresentation userRepresentation) {
        CredentialRepresentation credential = userRepresentation.getCredentials().get(0);

        String password = credential.getValue();

        mailSenderService.sendSimpleMessage(userRepresentation.getEmail(), "Password from keycloak", password);
    }

    private String getCreatedId(Response response) throws KeycloakException {
        URI location = response.getLocation();

        if (!response.getStatusInfo().equals(Response.Status.CREATED)) {
            Response.StatusType statusInfo = response.getStatusInfo();
            throw new KeycloakException("Create method returned status " +
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

    private void setRole(Response response, String role) throws KeycloakException {
        RoleResource roleResource = realm.roles().get(role);
        UserResource userResource = realm.users().get(getCreatedId(response));

        RoleRepresentation rolesRepresentation = roleResource.toRepresentation();

        userResource.roles().realmLevel().add(Collections.singletonList(rolesRepresentation));
    }

}
