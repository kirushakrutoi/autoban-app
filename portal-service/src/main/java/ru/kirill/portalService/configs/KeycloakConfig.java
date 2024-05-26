package ru.kirill.portalService.configs;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakConfig {
    private final String KEYCLOAK_URL = "http://localhost:8080";
    private final String KEYCLOAK_CLIENT_ID = "admin-cli";
    @Value("${keycloak.realm}")
    private String KEYCLOAK_REALM;
    @Value("${keycloak.admin.username}")
    private String KEYCLOAK_ADMIN_USERNAME;
    @Value("${keycloak.admin.password}")
    private String KEYCLOAK_ADMIN_PASSWORD;

    @Bean
    public Keycloak keycloak() {
        return KeycloakBuilder.builder()
                .serverUrl(KEYCLOAK_URL)
                .realm(KEYCLOAK_REALM)
                .clientId(KEYCLOAK_CLIENT_ID)
                .grantType(OAuth2Constants.PASSWORD)
                .username(KEYCLOAK_ADMIN_USERNAME)
                .password(KEYCLOAK_ADMIN_PASSWORD)
                .build();
    }

    @Bean
    public RealmResource realmResource(Keycloak keycloak){
        return keycloak.realm(KEYCLOAK_REALM);
    }
}
