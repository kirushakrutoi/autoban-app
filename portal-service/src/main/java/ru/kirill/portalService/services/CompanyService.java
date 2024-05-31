package ru.kirill.portalService.services;

import lombok.Data;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.kirill.portalService.exceptions.companyexceptions.CompanyNotCreatedException;
import ru.kirill.portalService.exceptions.companyexceptions.CompanyNotFoundException;
import ru.kirill.portalService.exceptions.companyexceptions.InvalidInnException;
import ru.kirill.portalService.exceptions.keycloakexceptions.ClientNotFoundException;
import ru.kirill.portalService.exceptions.keycloakexceptions.KeycloakException;
import ru.kirill.portalService.mappers.Mapper;
import ru.kirill.portalService.model.DTOs.*;
import ru.kirill.portalService.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Data
public class CompanyService {
    private KeycloakService keycloakService;
    private final Keycloak keycloak;
    private final RealmResource realm;
    @Value("${keycloak.realm}")
    private String KEYCLOAK_REALM;
    private String ROLE_REGISTER = "REGISTER";
    private MailSenderService mailSenderService;
    @Autowired
    private AdataService adataService;

    @Autowired
    public CompanyService(Keycloak keycloak, RealmResource realm, MailSenderService mailSenderService, AdataService adataService, KeycloakService keycloakService) {
        this.keycloak = keycloak;
        this.realm = realm;
        this.mailSenderService = mailSenderService;
        this.keycloakService = keycloakService;
    }

    public void createCompany(AdataDto adataDto, User user) throws CompanyNotCreatedException, InvalidInnException {
        try {
            CompanyDTO companyDTO = adataService.getInfoByInn(adataDto);
            if(companyDTO.getName() == null || companyDTO.getName().isEmpty()){
                throw new InvalidInnException("Incorrect inn");
            }
            ClientRepresentation clientRepresentation = Mapper.convertToClientRepresentation(companyDTO);
            keycloakService.createCompany(clientRepresentation, user);
        } catch (KeycloakException e){
            throw new CompanyNotCreatedException(e.getMessage(),
                    (HttpStatus) HttpStatusCode.valueOf(e.getResponse().getStatus()));
        }
    }


    public FullCompanyDTO getCompany(GetCompanyDTO getCompanyDTO) throws CompanyNotFoundException {
        try {
            ClientRepresentation clientRepresentation = keycloakService.getClientResourceById(getCompanyDTO.getName()).toRepresentation();
            List<UserRepresentation> users = keycloakService.getAllUSer();
            long countDriver = contUserByRole(users, getCompanyDTO.getName(), "DRIVER");
            long countLogist = contUserByRole(users, getCompanyDTO.getName(), "LOGIST");

            return Mapper.getCompanyFromRepresentation(clientRepresentation, countDriver, countLogist);
        } catch (ClientNotFoundException e){
            throw new CompanyNotFoundException("Company not found");
        }
    }

    public List<MinCompanyDTO> getCompanies(GetPagingDTO pagingDTO){
        List<ClientRepresentation> allClients = keycloakService.getAllClients();
        List<ClientRepresentation> companies = allClients.subList(8, allClients.size());
        List<MinCompanyDTO> companyDTOList = new ArrayList<>();

        for(ClientRepresentation clientRepresentation : companies){
            companyDTOList.add(Mapper.getCompanyFromRepresentation(clientRepresentation));
        }
        int fromIndex = pagingDTO.getPage() * pagingDTO.getPageSize();
        int toIndex = fromIndex + pagingDTO.getPageSize();

        return companyDTOList.subList(fromIndex, toIndex);
    }

    private long contUserByRole(List<UserRepresentation> users, String companyName, String role){
        long count = 0;
        for (UserRepresentation user : users) {
            try {
                List<String> asd = user.getAttributes().get(companyName);
                String atr = asd.get(0);

                if (atr.equals(role))
                    count++;
            } catch (NullPointerException ignored) {
            }
        }
        return count;
    }

}
