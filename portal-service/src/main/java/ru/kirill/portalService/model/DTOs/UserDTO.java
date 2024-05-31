package ru.kirill.portalService.model.DTOs;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.hibernate.validator.constraints.pl.NIP;

import java.util.Map;

@Data
public class UserDTO {
    @NotEmpty(message = "Username can't be empty")
    private String username;
    @NotEmpty(message = "Company name can't be empty")
    private String companyName;
    @NotEmpty(message = "Company role can't be empty")
    private String companyRole;
}
