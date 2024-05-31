package ru.kirill.portalService.services;

import jakarta.ws.rs.core.Response;
import lombok.Data;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.kirill.portalService.mappers.Mapper;
import ru.kirill.portalService.model.DTOs.*;
import ru.kirill.portalService.model.User;

import java.util.ArrayList;
import java.util.List;

@Service
@Data
public class UserService {
    private final KeycloakService keycloakService;
    private final Keycloak keycloak;
    private final RealmResource realm;
    @Value("${keycloak.realm}")
    private String KEYCLOAK_REALM;
    private String ROLE_REGISTER = "REGISTER";
    private MailSenderService mailSenderService;
    @Autowired
    private AdataService adataService;

    @Autowired
    public UserService(Keycloak keycloak, RealmResource realm, MailSenderService mailSenderService, AdataService adataService, KeycloakService keycloakService) {
        this.keycloak = keycloak;
        this.realm = realm;
        this.mailSenderService = mailSenderService;
        this.keycloakService = keycloakService;
    }

    public ResponseEntity<HttpStatus> createRegister(RegisterDTO registerDTO){
        UserRepresentation userRepresentation = Mapper.convertToUserRepresentation(registerDTO);
        return keycloakService.addUser(userRepresentation);
    }

    public ResponseEntity<HttpStatus> addClientRoleForExistUser(UserDTO userDto, User adminUser){
        String companyName = userDto.getCompanyName();
        String companyRole = userDto.getCompanyRole();

        if(!checkAuthority(companyName, adminUser, companyRole))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        setClientRole(companyName, companyRole, userDto.getUsername());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<HttpStatus> createUserAndAddClientRole(NewUserDTO newUserDTO, User adminUser){
        String companyName = newUserDTO.getCompanyName();
        String companyRole = newUserDTO.getCompanyRole();

        if(!checkAuthority(companyName, adminUser, companyRole))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        createRegister(newUserDTO);

        setClientRole(companyName, companyRole, newUserDTO.getUsername());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<HttpStatus> resetPassword(ResetPasswordDTO passwordDTO, User user) {
        UserResource userResource = keycloakService.getUserResource(user.getUserId());

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setTemporary(false);
        credential.setValue(passwordDTO.getNewPassword());

        userResource.resetPassword(credential);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<HttpStatus> changeData(ChangeDataDTO changeDataDTO, User user){
        UserResource userResource = keycloakService.getUserResource(user.getUserId());
        UserRepresentation userRepresentation = userResource.toRepresentation();

        changeUserRepresentation(changeDataDTO, userRepresentation);

        userResource.update(userRepresentation);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    public List<UserDTO> getUsers(GetCompanyDTO companyDTO, User user){
        if(!user.getClientRoles().containsKey(companyDTO.getName())){
            return null;
        }

        if(!user.getClientRoles().get(companyDTO.getName()).equals("ADMIN")){
            return null;
        }

        List<UserRepresentation> users = keycloakService.getUserHasClientRole(companyDTO.getName());

        List<UserRepresentation> usersFromCompany = getUsersFromCompany(users, companyDTO.getName());

        List<UserDTO> userDTOS = new ArrayList<>();

        for(UserRepresentation userRepresentation : usersFromCompany){
            userDTOS.add(Mapper.getUserDTOFromRepresentation(userRepresentation, companyDTO.getName()));
        }

        return userDTOS;

    }

    public List<UserRepresentation> getUsersFromCompany(List<UserRepresentation> usersRepresentation, String companyName){
        List<UserRepresentation> users = new ArrayList<>();
        for (UserRepresentation user : usersRepresentation){
            if(user.getAttributes().containsKey(companyName))
                users.add(user);
        }
        return users;
    }
    public void changeUserRepresentation(ChangeDataDTO changeDataDTO, UserRepresentation userRepresentation){
        if(changeDataDTO.getFirstName() != null)
            userRepresentation.setFirstName(changeDataDTO.getFirstName());

        if(changeDataDTO.getLastName() != null)
            userRepresentation.setLastName(changeDataDTO.getLastName());

        if(changeDataDTO.getEmail() != null)
            userRepresentation.setEmail(changeDataDTO.getEmail());
    }

    public void setClientRole(String companyName, String companyRole, String username){
        String id = keycloakService.getUserIdByUserName(username);
        ClientResource clientResource = keycloakService.getClientResourceById(companyName);
        String clientID = keycloakService.getClientIdByName(companyName);
        keycloakService.deleteClientRole(id, clientID);
        keycloakService.addClientRole(id, clientResource, clientID, companyRole);
    }

    public boolean checkAuthority(String companyName, User adminUser, String newUserCompanyRole){
        if(!adminUser.getClientRoles().containsKey(companyName))
            return false;
        String userClientRole = adminUser.getClientRoles().get(companyName);

        if(userClientRole.equals("DRIVER"))
            return false;

        if(userClientRole.equals("LOGIST") && !newUserCompanyRole.equals("DRIVER"))
            return false;

        return true;
    }
}
