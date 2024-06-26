package ru.kirill.logistService.models.DTOs;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.kirill.logistService.models.Point;

@Data
public class TaskDTO {
    @NotEmpty
    private String companyName;
    @NotNull
    private Point startPoint;
    @NotNull
    private Point endPoint;
    @NotNull
    private DriverDTO driverDTO;
    @NotEmpty
    private String orderDescription;
    @NotNull
    private CarDTO carDTO;
}
