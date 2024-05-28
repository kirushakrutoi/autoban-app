package ru.kirill.gatewayService.filters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ru.kirill.gatewayService.security.UserDetailsImpl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class PortalGatewayFilterFactory extends AbstractGatewayFilterFactory {
    @Override
    public GatewayFilter apply(Object config) {
            return (exchange, chain) -> ReactiveSecurityContextHolder.getContext()
                    .filter(securityContext -> securityContext.getAuthentication() != null)
                    .flatMap(securityContext -> {
                        UserDetailsImpl userInfo = (UserDetailsImpl) securityContext.getAuthentication().getPrincipal();
                        ServerHttpRequest request = null;
                        try {
                            String json = new ObjectMapper().writeValueAsString(userInfo.getClientRoles());
                            String encodedHeader = Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
                            request = exchange.getRequest().mutate()
                                    .header("username", userInfo.getUsername())
                                    .header("userid", userInfo.getId())
                                    .header("clientroles", encodedHeader)
                                    .build();
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                        ServerWebExchange modifiedExchange = exchange.mutate().request(request).build();
                        return chain.filter(modifiedExchange);
                    });
    }
}
