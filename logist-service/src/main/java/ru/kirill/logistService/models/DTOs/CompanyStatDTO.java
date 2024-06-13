package ru.kirill.logistService.models.DTOs;

import lombok.Data;

@Data
public class CompanyStatDTO {
    private String companyName;
    private long countEndedRoute;
    private long countCanceledRoute;
    private long countStartedRoute;
    private long countTask;
}
