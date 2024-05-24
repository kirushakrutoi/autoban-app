package ru.kirill.gatewayService.controllers;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RoleMappingResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/hello")
public class controller {
    @Autowired
    Keycloak keycloak;
    @GetMapping
    public java.lang.String getHello(){
        return "hello";
    }

    @GetMapping("/admin")
    public String admin(){
        return "hello admin";
    }
}
