package ru.kirill.portalService.services;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.kirill.portalService.model.DTOs.AdataDto;
import ru.kirill.portalService.model.DTOs.CompanyDTO;

import java.net.URI;
import java.util.Collections;

@Service
public class AdataService {
    private final String URI = "http://suggestions.dadata.ru/suggestions/api/4_1/rs/findById/party";
    private final String HEADER_NAME = "Authorization";
    @Value("${adata.token}")
    private String HEADER_VALUE;


    public CompanyDTO getInfoByInn(AdataDto adataDto){
        RestTemplate restTemplate = new RestTemplate();

        ClientHttpRequestInterceptor interceptor = (request, body, execution) -> {
            HttpHeaders headers = request.getHeaders();
            headers.add(HEADER_NAME, HEADER_VALUE);
            return execution.execute(request, body);
        };

        restTemplate.setInterceptors(Collections.singletonList(interceptor));

        ResponseEntity<CompanyDTO> response = restTemplate.exchange(
                URI,
                HttpMethod.POST,
                new HttpEntity<>(adataDto),
                CompanyDTO.class);

        return response.getBody();
    }
}
