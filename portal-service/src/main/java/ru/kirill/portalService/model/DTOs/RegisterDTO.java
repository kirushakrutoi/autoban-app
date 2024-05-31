package ru.kirill.portalService.model.DTOs;

import jakarta.annotation.sql.DataSourceDefinition;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterDTO {
    @NotEmpty(message = "Username can't be empty")
    @Size(min = 3, max = 30, message = "Not valid username")
    private String username;
    @NotEmpty(message = "Firstname can't be empty")
    @Size(min = 3, max = 30, message = "Not valid firstname")
    private String firstName;
    @NotEmpty(message = "Lastname can't be empty")
    @Size(min = 3, max = 30, message = "Not valid lastname")
    private String lastName;
    @Email(message = "Not valid email")
    private String email;
}
