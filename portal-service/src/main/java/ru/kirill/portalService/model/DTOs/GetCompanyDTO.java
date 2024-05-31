package ru.kirill.portalService.model.DTOs;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class GetCompanyDTO {
    @NotEmpty(message = "Company name can't be empty")
    public String name;
}
