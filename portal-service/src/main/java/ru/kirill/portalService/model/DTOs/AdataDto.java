package ru.kirill.portalService.model.DTOs;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdataDto {
    @NotEmpty(message = "Query can't be empty")
    private String query;
}
