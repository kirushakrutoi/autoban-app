package ru.kirill.portalService.model.DTOs;

import lombok.Data;

import java.util.Map;

@Data
public class UserDTO {
    private String username;
    private String companyName;
    private String companyRole;
}
