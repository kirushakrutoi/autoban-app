package ru.kirill.logistService.models.DTOs;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.apache.kafka.common.protocol.types.Field;
import ru.kirill.logistService.models.Point;

@Data
public class CreateTaskDTO {
    @NotEmpty
    private String companyName;
    @NotNull
    private Point startPoint;
    @NotNull
    private Point endPoint;
    @NotNull
    private String driverId;
    @NotEmpty
    private String orderDescription;
    @NotNull
    private long carId;
}
