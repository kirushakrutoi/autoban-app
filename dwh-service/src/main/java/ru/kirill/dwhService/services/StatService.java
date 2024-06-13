package ru.kirill.dwhService.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kirill.dwhService.exceptions.CompanyNotFoundException;
import ru.kirill.dwhService.exceptions.ForbiddenException;
import ru.kirill.dwhService.models.CompanyStat;
import ru.kirill.dwhService.repositories.StatRepository;
import ru.kirill.models.User;

import java.util.List;
import java.util.Optional;

@Service
public class StatService {
    @Autowired
    private StatRepository statRepository;

    @Transactional(readOnly = true)
    public CompanyStat getStat(String companyName, User user) throws ForbiddenException, CompanyNotFoundException {
            Optional<CompanyStat> OStat = statRepository.findByCompanyName(companyName);

            if(!OStat.isPresent())
                throw new CompanyNotFoundException("No such company has been found");


            if(!checkAuthority(user, companyName, "ADMIN")
                    && !checkAuthority(user, companyName, "LOGIST"))
                throw new ForbiddenException("Нou are not LOGIST or ADMIN of this company");

            return OStat.get();
    }

    public void saveStat(CompanyStat companyStat){
        statRepository.save(companyStat);
    }

    @Transactional
    public void saveStats(List<CompanyStat> companyStats){
        for(CompanyStat companyStat : companyStats){
            saveStat(companyStat);
        }
    }

    private boolean checkAuthority(User user, String companyName, String role) {
        if(!user.getClientRoles().containsKey(companyName))
            return false;

        if(!user.getClientRoles().get(companyName).equals(role))
            return false;

        return true;
    }
}
