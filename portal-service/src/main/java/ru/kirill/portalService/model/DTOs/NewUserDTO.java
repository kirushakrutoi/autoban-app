package ru.kirill.portalService.model.DTOs;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class NewUserDTO extends RegisterDTO{
    @NotEmpty(message = "Company name can't be empty")
    private String companyName;
    @NotEmpty(message = "Company role can't be empty")
    private String companyRole;
}
