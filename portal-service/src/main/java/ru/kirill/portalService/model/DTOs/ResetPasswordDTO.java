package ru.kirill.portalService.model.DTOs;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.bind.annotation.PostMapping;

@Data
public class ResetPasswordDTO {
    @NotEmpty(message = "Password can't be empty")
    @Size(min = 6, message = "The password must not be shorter than 6 characters")
    private String newPassword;
}
