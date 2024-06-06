package ru.kirill.logistService.models.DTOs;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreatePointDTO {
    private long routeId;
    private BigDecimal lat;
    private BigDecimal lon;
}
