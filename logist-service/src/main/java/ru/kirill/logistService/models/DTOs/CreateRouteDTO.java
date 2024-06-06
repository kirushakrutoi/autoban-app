package ru.kirill.logistService.models.DTOs;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateRouteDTO {
    @NotNull
    private long taskId;
}
