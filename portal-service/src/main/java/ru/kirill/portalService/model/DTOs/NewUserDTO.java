package ru.kirill.portalService.model.DTOs;

import lombok.Data;

@Data
public class NewUserDTO extends RegisterDTO{
    private String companyName;
    private String companyRole;
}
