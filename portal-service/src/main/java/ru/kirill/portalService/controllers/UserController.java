package ru.kirill.portalService.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kirill.portalService.mappers.Mapper;
import ru.kirill.portalService.model.DTOs.NewUserDTO;
import ru.kirill.portalService.model.DTOs.RegisterDTO;
import ru.kirill.portalService.model.DTOs.ResetPasswordDTO;
import ru.kirill.portalService.model.DTOs.UserDTO;
import ru.kirill.portalService.services.AdataService;
import ru.kirill.portalService.services.KeycloakService;
import ru.kirill.portalService.services.UserService;

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
    public ResponseEntity<HttpStatus> createRegister(@RequestBody RegisterDTO registerDTO) {
        return userService.createRegister(registerDTO);
    }

    @PostMapping("/add/client/role")
    public ResponseEntity<HttpStatus> addClientRoleForUser(@RequestBody UserDTO userDTO,
                                              @RequestHeader HttpHeaders headers) throws JsonProcessingException {
        userService.addClientRoleForExistUser(userDTO, Mapper.getUserFromHeaders(headers));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/create/add/client/role")
    public ResponseEntity<HttpStatus> createUserAndAddClientRole(@RequestBody NewUserDTO newUserDTO,
                                                                 @RequestHeader HttpHeaders headers) throws JsonProcessingException {
        return userService.createUserAndAddClientRole(newUserDTO, Mapper.getUserFromHeaders(headers));
    }

    @PostMapping("/reset/password")
    public ResponseEntity<HttpStatus> resetPassword(@RequestBody ResetPasswordDTO passwordDTO,
                                                    @RequestHeader HttpHeaders headers) throws JsonProcessingException {
        return userService.resetPassword(passwordDTO, Mapper.getUserFromHeaders(headers));
    }
}
