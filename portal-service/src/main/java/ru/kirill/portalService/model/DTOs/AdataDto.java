package ru.kirill.portalService.model.DTOs;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class AdataDto {
    @NotEmpty(message = "Query can't be empty")
    private String query;
}
