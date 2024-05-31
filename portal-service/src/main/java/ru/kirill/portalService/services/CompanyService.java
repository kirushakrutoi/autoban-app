package ru.kirill.portalService.services;

import lombok.Data;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
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

    public ResponseEntity<HttpStatus> createCompany(AdataDto adataDto, User user){
        CompanyDTO companyDTO = adataService.getInfoByInn(adataDto);
        ClientRepresentation clientRepresentation = Mapper.convertToClientRepresentation(companyDTO);
        return keycloakService.createCompany(clientRepresentation, user);
    }


    public FullCompanyDTO getCompany(GetCompanyDTO getCompanyDTO) {
        CompanyDTO companyDTO = new CompanyDTO();
        ClientRepresentation clientRepresentation = keycloakService.getClientResourceById(getCompanyDTO.getName()).toRepresentation();
        List<UserRepresentation> users = keycloakService.getUserHasClientRole(getCompanyDTO.name);
        long countDriver = contUserByRole(users, getCompanyDTO.getName(), "DRIVER");
        long countLogist = contUserByRole(users, getCompanyDTO.getName(), "LOGIST");

        return Mapper.getCompanyFromRepresentation(clientRepresentation, countDriver, countLogist);
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
