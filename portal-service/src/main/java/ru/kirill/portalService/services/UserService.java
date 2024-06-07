package ru.kirill.portalService.services;

import jakarta.ws.rs.core.Response;
import lombok.Data;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleMappingResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.kirill.portalService.exceptions.companyexceptions.CompanyNotFoundException;
import ru.kirill.portalService.exceptions.keycloakexceptions.ClientNotFoundException;
import ru.kirill.portalService.exceptions.keycloakexceptions.KeycloakException;
import ru.kirill.portalService.exceptions.userexception.ForbiddenException;
import ru.kirill.portalService.exceptions.userexception.RoleNotSetException;
import ru.kirill.portalService.exceptions.userexception.UserNotCreatedException;
import ru.kirill.portalService.exceptions.userexception.UserNotFoundException;
import ru.kirill.portalService.mappers.Mapper;
import ru.kirill.portalService.model.DTOs.*;
import ru.kirill.portalService.model.User;

import java.util.*;

@Service
@Data
public class UserService {
    private final KeycloakService keycloakService;
    private final RealmResource realm;
    @Value("${keycloak.realm}")
    private String KEYCLOAK_REALM;
    private String ROLE_REGISTER = "REGISTER";
    private MailSenderService mailSenderService;
    @Autowired
    private AdataService adataService;

    @Autowired
    public UserService(RealmResource realm, MailSenderService mailSenderService, AdataService adataService, KeycloakService keycloakService) {
        this.realm = realm;
        this.mailSenderService = mailSenderService;
        this.keycloakService = keycloakService;
    }

    public void createRegister(RegisterDTO registerDTO) throws UserNotCreatedException, UserNotFoundException {
        UserRepresentation userRepresentation = Mapper.convertToUserRepresentation(registerDTO);
        try {
            keycloakService.addUser(userRepresentation);
        } catch (KeycloakException e) {
            throw new UserNotCreatedException(e.getMessage(),
                    (HttpStatus) HttpStatusCode.valueOf(e.getResponse().getStatus()));
        }
    }

    public void addClientRoleForExistUser(UserDTO userDto, User adminUser) throws RoleNotSetException {
        String companyName = userDto.getCompanyName();
        String companyRole = userDto.getCompanyRole();

        if(!checkAuthority(companyName, adminUser, companyRole))
            throw new RoleNotSetException("You don't have the necessary authority", HttpStatus.FORBIDDEN);

        setClientRole(companyName, companyRole, userDto.getUsername());
    }

    public void createUserAndAddClientRole(NewUserDTO newUserDTO, User adminUser) throws UserNotCreatedException, RoleNotSetException, UserNotFoundException {
        String companyName = newUserDTO.getCompanyName();
        String companyRole = newUserDTO.getCompanyRole();

        if(!checkAuthority(companyName, adminUser, companyRole))
            throw new UserNotCreatedException("You don't have the necessary authority", HttpStatus.FORBIDDEN);

        createRegister(newUserDTO);

        setClientRole(companyName, companyRole, newUserDTO.getUsername());
    }

    public void resetPassword(ResetPasswordDTO passwordDTO, User user) throws UserNotFoundException {
        UserResource userResource = keycloakService.getUserResource(user.getUserId());

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setTemporary(false);
        credential.setValue(passwordDTO.getNewPassword());

        userResource.resetPassword(credential);
    }

    public void changeData(ChangeDataDTO changeDataDTO, User user) throws UserNotFoundException {
        UserResource userResource = keycloakService.getUserResource(user.getUserId());
        UserRepresentation userRepresentation = userResource.toRepresentation();

        changeUserRepresentation(changeDataDTO, userRepresentation);

        userResource.update(userRepresentation);
    }

    public List<UserDTO> getUsers(GetCompanyDTO companyDTO, User user) throws ForbiddenException, CompanyNotFoundException {
        try {
            keycloakService.getClientIdByName(companyDTO.getName());
        } catch (ClientNotFoundException e){
            throw new CompanyNotFoundException("Company not found");
        }

        if(!user.getClientRoles().containsKey(companyDTO.getName())){
            throw new ForbiddenException("You don't have the necessary authority");
        }

        if(!user.getClientRoles().get(companyDTO.getName()).equals("ADMIN")){
            throw new ForbiddenException("You don't have the necessary authority");
        }


        List<UserRepresentation> users = keycloakService.getAllUSer();

        List<UserRepresentation> usersFromCompany = getUsersFromCompany(users, companyDTO.getName());

        List<UserDTO> userDTOS = new ArrayList<>();

        for(UserRepresentation userRepresentation : usersFromCompany){
            userDTOS.add(Mapper.getUserDTOFromRepresentation(userRepresentation, companyDTO.getName()));
        }

        return userDTOS;
    }

    public DriverDTO getDriver(String id, User user, String companyNane) throws UserNotFoundException, ForbiddenException {
        UserResource userResource = keycloakService.getUserResource(id);
        Map<String, ClientMappingsRepresentation> roles = userResource.roles().getAll().getClientMappings();

        boolean flag = false;
        for(Map.Entry<String, ClientMappingsRepresentation> entry : roles.entrySet()){
            if(entry.getValue().getClient().equals(companyNane) &&
                entry.getValue().getMappings().get(0).toString().equals("DRIVER") &&
                user.getClientRoles().containsKey(companyNane))
                flag = true;
        }

        if(!flag)
            throw new ForbiddenException("You are not a LOGIST of this company");

        UserRepresentation driver = userResource.toRepresentation();

        return Mapper.convertToDriverDTO(driver);
    }

    public List<UserRepresentation> getUsersFromCompany(List<UserRepresentation> usersRepresentation, String companyName){
        List<UserRepresentation> users = new ArrayList<>();
        for (UserRepresentation user : usersRepresentation){
            Map<String, List<String>> attributes = Optional.ofNullable(user.getAttributes()).orElse(new HashMap<>());
            if(attributes.containsKey(companyName))
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

    public void setClientRole(String companyName, String companyRole, String username) throws RoleNotSetException {
        try {
            String id = keycloakService.getUserIdByUserName(username);
            ClientResource clientResource = keycloakService.getClientResourceById(companyName);
            String clientID = keycloakService.getClientIdByName(companyName);
            keycloakService.deleteClientRole(id, clientID, companyName);
            keycloakService.addClientRole(id, clientResource, clientID, companyRole,companyName);
        } catch (Exception e){
            throw new RoleNotSetException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

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
