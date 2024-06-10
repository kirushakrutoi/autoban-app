package ru.kirill.driverService.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kirill.driverService.clients.LogistClient;
import ru.kirill.models.DTOs.CreateRouteDTO;

@RestController
@RequestMapping("/route")
public class RouteController {
    @Autowired
    private LogistClient logistClient;

    @PostMapping("")
    public ResponseEntity<String> create(@RequestBody @Valid CreateRouteDTO routeDTO,
                                         @RequestHeader HttpHeaders headers){
        try {
            logistClient.createRoute(headers.toSingleValueMap(), routeDTO);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e){
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
