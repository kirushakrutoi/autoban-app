package ru.kirill.logistService.models;

import lombok.Data;

import java.util.Map;

@Data
public class User {
    private String userId;
    private String username;
    private Map<String, String> clientRoles;
}
