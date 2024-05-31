package ru.kirill.portalService.model.DTOs;

import lombok.Data;

@Data
public class FullCompanyDTO extends CompanyDTO{
    private long countDriver;
    private long countLogist;
    private String id;
}
