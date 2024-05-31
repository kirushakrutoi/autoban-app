package ru.kirill.portalService.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    @PostMapping("/change/data")
    public ResponseEntity<HttpStatus> changeData(@RequestBody ChangeDataDTO changeDataDTO,
                                                 @RequestHeader HttpHeaders headers) throws JsonProcessingException {
        return userService.changeData(changeDataDTO, Mapper.getUserFromHeaders(headers));
    }

    @GetMapping("/get")
    public ResponseEntity<List<UserDTO>> getUser(@RequestBody GetCompanyDTO companyDTO,
                                                 @RequestHeader HttpHeaders headers){

    }
}
