package ru.kirill.gatewayService.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.ClientMappingsRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.RolesRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@Data
@Component
public class AccountAuthenticationProvider implements ReactiveAuthenticationManager {
    @Value("${keycloak.realm}")
    private String KEYCLOAK_REALM;
    private final String USER_ID = "sub";
    private final Keycloak keycloak;
    private final ReactiveJwtDecoder jwtDecoder;

    @Autowired
    public AccountAuthenticationProvider(Keycloak keycloak, ReactiveJwtDecoder jwtDecoder) {
        this.keycloak = keycloak;
        this.jwtDecoder = jwtDecoder;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        BearerTokenAuthenticationToken authenticationToken = (BearerTokenAuthenticationToken) authentication;

        Mono<String> userId = getJwt(authenticationToken).map(jwt ->
            jwt.getClaimAsString(USER_ID)
        );

        Mono<UserDetails> user = userId.map(id -> {
            UserResource userResource = keycloak.realm(KEYCLOAK_REALM).users().get(id);

            return getUserDetails(userResource);
        });

        return user.map(userDetails -> new UsernamePasswordAuthenticationToken(
                userDetails,
                authenticationToken.getCredentials(),
                userDetails.getAuthorities()
        ));
    }

    private Mono<Jwt> getJwt(BearerTokenAuthenticationToken bearer){
        return this.jwtDecoder.decode(bearer.getToken());
    }

    private UserDetails getUserDetails(UserResource user) {
        UserRepresentation userRepresentation = user.toRepresentation();
        String userId = userRepresentation.getId();
        String username = userRepresentation.getUsername();
        Set<String> roles = getRoles(user);
        Map<String, String> clientRoles = getClientRoles(user);

        return new UserDetailsImpl(userId, username, roles, clientRoles);
    }

    private Map<String, String> getClientRoles(UserResource user) {
        Map<String, ClientMappingsRepresentation> clientListRoles = user.roles().getAll().getClientMappings();
        Map<String, String> clientRoles = new HashMap<>();

        if(clientListRoles == null)
            return new HashMap<>();

        for(Map.Entry<String, ClientMappingsRepresentation> entry : clientListRoles.entrySet()){
            String clientId = entry.getValue().getClient();
            String role = entry.getValue().getMappings().get(0).getName();

            clientRoles.put(clientId, role);
        }
        return clientRoles;
    }

    private Set<String> getRoles(UserResource user){
        List<RoleRepresentation> roles = user.roles().getAll().getRealmMappings();

        return roles.stream()
                .map(RoleRepresentation::getName)
                .collect(Collectors.toSet());
    }
}
