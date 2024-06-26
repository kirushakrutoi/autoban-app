package ru.kirill.driverService.configs;

import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.kirill.driverService.clients.LogistClient;

@Configuration
public class LogistClientConfig {
    @Bean
    public LogistClient portalClient(){

        return Feign.builder()
                .client(new OkHttpClient())
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .target(LogistClient.class, "http://localhost:8081/logist/");

    }
}
