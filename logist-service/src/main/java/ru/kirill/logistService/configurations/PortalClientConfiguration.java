package ru.kirill.logistService.configurations;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;
import jakarta.persistence.Column;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.kirill.logistService.services.PortalClient;

@Configuration
public class PortalClientConfiguration {

    @Bean
    public PortalClient portalClient(){

        return Feign.builder()
                .client(new OkHttpClient())
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .target(PortalClient.class, "http://localhost:8081/portal/");

    }
}
