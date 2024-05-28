package ru.kirill.portalService.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.catalina.util.URLEncoder;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/hello")
public class Controllers {

    @GetMapping
    public String hello(@RequestHeader HttpHeaders headers) throws IOException {
        List<String> clientRoles =  headers.get("clientroles");
        String json = new String(Base64.getDecoder().decode(clientRoles.get(0)));

        Map<String, String> clientroles = new ObjectMapper().readerFor(Map.class).readValue(json);
        System.out.println(json);
        System.out.println(clientRoles.get(0));
        System.out.println(clientroles);
        return "hello from portal";
    }
}
