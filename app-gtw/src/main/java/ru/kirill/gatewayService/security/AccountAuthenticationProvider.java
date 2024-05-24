package ru.kirill.gatewayService.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.RolesRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
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
@AllArgsConstructor
@Component
public class AccountAuthenticationProvider implements ReactiveAuthenticationManager {
    private final Keycloak keycloak;
    private final ReactiveJwtDecoder jwtDecoder;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        BearerTokenAuthenticationToken authenticationToken = (BearerTokenAuthenticationToken) authentication;

        Mono<String> userId = getJwt(authenticationToken).map(jwt ->
            jwt.getClaimAsString("sub")
        );

        Mono<UserDetails> user = userId.map(id -> {
            UserResource userResource = keycloak.realm("testRealm").users().get(id);

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

        return new UserDetailsImpl(userId, username, roles);
    }

    private Set<String> getRoles(UserResource user){
        List<RoleRepresentation> roles = user.roles().getAll().getRealmMappings();

        return roles.stream()
                .map(RoleRepresentation::getName)
                .collect(Collectors.toSet());
    }
}
