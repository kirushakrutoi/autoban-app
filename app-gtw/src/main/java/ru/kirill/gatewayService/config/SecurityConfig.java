package ru.kirill.gatewayService.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import reactor.core.publisher.Mono;
import ru.kirill.gatewayService.security.AccountAuthenticationProvider;

@Configuration
public class SecurityConfig {
    @Autowired
    private AccountAuthenticationProvider authenticationProvider;
    @Bean
    public SecurityWebFilterChain configure(ServerHttpSecurity http){
        return http
                .authenticationManager(authenticationProvider)
                .authorizeExchange(configure -> configure
                        .pathMatchers("/portal/company/create").hasAuthority("REGISTER")
                        .pathMatchers("/portal/user/create/register").permitAll()
                        .pathMatchers("/openid-connect/**").permitAll()
                        .pathMatchers("/hello/admin").hasAuthority("ROLE_ADMIN")
                        .pathMatchers("/hello/user").hasAuthority("USER")
                        .anyExchange().authenticated())
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .oauth2ResourceServer(c -> c.authenticationManagerResolver(context -> Mono.just(authenticationProvider)))
                .build();
    }
}
