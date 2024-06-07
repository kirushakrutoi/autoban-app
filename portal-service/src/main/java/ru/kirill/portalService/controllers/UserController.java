package ru.kirill.portalService.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import jakarta.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.kirill.portalService.exceptions.companyexceptions.CompanyNotFoundException;
import ru.kirill.portalService.exceptions.userexception.ForbiddenException;
import ru.kirill.portalService.exceptions.userexception.RoleNotSetException;
import ru.kirill.portalService.exceptions.userexception.UserNotCreatedException;
import ru.kirill.portalService.exceptions.userexception.UserNotFoundException;
import ru.kirill.portalService.mappers.Mapper;
import ru.kirill.portalService.model.DTOs.*;
import ru.kirill.portalService.services.AdataService;
import ru.kirill.portalService.services.KeycloakService;
import ru.kirill.portalService.services.UserService;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private KeycloakService keycloakService;
    @Autowired
    private AdataService adataService;

    @PostMapping("/create/register")
    public ResponseEntity<String> createRegister(@RequestBody @Valid RegisterDTO registerDTO,
                                                 BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return new ResponseEntity<>(getErrorMessage(bindingResult), HttpStatus.BAD_REQUEST);
        }

        try {
            userService.createRegister(registerDTO);
            return  new ResponseEntity<>("The user has been successfully created", HttpStatus.CREATED);
        } catch (UserNotCreatedException e) {
            return new ResponseEntity<>(e.getMessage(), e.getStatus());
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/add/client/role")
    public ResponseEntity<String> addClientRoleForUser(@RequestBody @Valid UserDTO userDTO,
                                                       @RequestHeader HttpHeaders headers,
                                                       BindingResult bindingResult) throws JsonProcessingException {
        if(bindingResult.hasErrors()){
            return new ResponseEntity<>(getErrorMessage(bindingResult), HttpStatus.BAD_REQUEST);
        }

        try {
            userService.addClientRoleForExistUser(userDTO, Mapper.getUserFromHeaders(headers));
            return new ResponseEntity<>("The role was successfully assigned",HttpStatus.OK);
        } catch (RoleNotSetException e){
            return new ResponseEntity<>(e.getMessage(),e.getStatus());
        }
    }

    @PostMapping("/create/add/client/role")
    public ResponseEntity<String> createUserAndAddClientRole(@RequestBody @Valid NewUserDTO newUserDTO,
                                                             @RequestHeader HttpHeaders headers,
                                                             BindingResult bindingResult) throws JsonProcessingException {
        if(bindingResult.hasErrors()){
            return new ResponseEntity<>(getErrorMessage(bindingResult), HttpStatus.BAD_REQUEST);
        }

        try {
            userService.createUserAndAddClientRole(newUserDTO, Mapper.getUserFromHeaders(headers));
            return new ResponseEntity<>("The user has been successfully created", HttpStatus.CREATED);
        } catch (UserNotCreatedException e) {
            return new ResponseEntity<>(e.getMessage(), e.getStatus());
        } catch (RoleNotSetException e) {
            return new ResponseEntity<>(e.getMessage(), e.getStatus());
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/reset/password")
    public ResponseEntity<String> resetPassword(@RequestBody @Valid ResetPasswordDTO passwordDTO,
                                                    @RequestHeader HttpHeaders headers,
                                                    BindingResult bindingResult) throws JsonProcessingException {
        if(bindingResult.hasErrors()){
            return new ResponseEntity<>(getErrorMessage(bindingResult), HttpStatus.BAD_REQUEST);
        }
        try {
            userService.resetPassword(passwordDTO, Mapper.getUserFromHeaders(headers));
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (UserNotFoundException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/change/data")
    public ResponseEntity<String> changeData(@RequestBody ChangeDataDTO changeDataDTO,
                                                 @RequestHeader HttpHeaders headers) throws JsonProcessingException {
        try {
            userService.changeData(changeDataDTO, Mapper.getUserFromHeaders(headers));
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (UserNotFoundException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/get")
    public ResponseEntity<?> getUser(@RequestBody @Valid GetCompanyDTO companyDTO,
                                                 @RequestHeader HttpHeaders headers) throws JsonProcessingException {
        try {
            return new ResponseEntity<>(userService.getUsers(companyDTO, Mapper.getUserFromHeaders(headers)), HttpStatus.OK);
        } catch (CompanyNotFoundException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (ForbiddenException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }

    }

    @PostMapping("/get/driver/{id}")
    public ResponseEntity<?> getDriver(@PathVariable("id") String id,
                                       @RequestHeader HttpHeaders headers,
                                       @RequestBody GetCompanyDTO companyDTO){

        try {
            return new ResponseEntity<>(userService.getDriver(id, Mapper.getUserFromHeaders(headers), companyDTO.getName()), HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (ForbiddenException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private String getErrorMessage(BindingResult bindingResult){
        StringBuilder stringBuilder = new StringBuilder();

        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            stringBuilder.append(fieldError.getField())
                    .append(" - ")
                    .append(fieldError.getDefaultMessage())
                    .append("; ");
        }

        return stringBuilder.toString();
    }
}
