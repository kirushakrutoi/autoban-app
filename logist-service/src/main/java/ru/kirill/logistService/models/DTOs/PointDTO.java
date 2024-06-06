package ru.kirill.logistService.models.DTOs;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PointDTO {
    private BigDecimal lat;
    private BigDecimal lon;
}
