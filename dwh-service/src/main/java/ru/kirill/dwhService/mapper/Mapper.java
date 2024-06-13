package ru.kirill.dwhService.mapper;

import ru.kirill.dwhService.models.CompanyStat;
import ru.kirill.dwhService.models.DTOs.CompanyStatDTO;

public class Mapper {
    public static CompanyStat convertToCompanyStat(CompanyStatDTO companyStatDTO){
        CompanyStat companyStat = new CompanyStat();

        companyStat.setCompanyName(companyStatDTO.getCompanyName());
        companyStat.setCountTask(companyStatDTO.getCountTask());
        companyStat.setCountCanceledRoute(companyStatDTO.getCountCanceledRoute());
        companyStat.setCountStartedRoute(companyStatDTO.getCountStartedRoute());
        companyStat.setCountEndedRoute(companyStatDTO.getCountEndedRoute());

        return companyStat;
    }
}
