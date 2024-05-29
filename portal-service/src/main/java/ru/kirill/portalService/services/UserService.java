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
import ru.kirill.portalService.model.DTOs.NewUserDTO;
import ru.kirill.portalService.model.DTOs.RegisterDTO;
import ru.kirill.portalService.model.DTOs.ResetPasswordDTO;
import ru.kirill.portalService.model.DTOs.UserDTO;
import ru.kirill.portalService.model.User;

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
