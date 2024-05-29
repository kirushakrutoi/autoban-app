package ru.kirill.portalService.model.DTOs;

import lombok.Data;
import org.springframework.web.bind.annotation.PostMapping;

@Data
public class ResetPasswordDTO {
    private String newPassword;
}
