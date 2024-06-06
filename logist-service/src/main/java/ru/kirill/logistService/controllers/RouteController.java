package ru.kirill.logistService.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.kirill.logistService.exceptions.ForbiddenException;
import ru.kirill.logistService.exceptions.IncorrectDataException;
import ru.kirill.logistService.exceptions.RouteNotFoundException;
import ru.kirill.logistService.exceptions.TaskNotFoundException;
import ru.kirill.logistService.mappers.Mapper;
import ru.kirill.logistService.models.DTOs.CreateRouteDTO;
import ru.kirill.logistService.services.RouteService;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/route")
public class RouteController {

    @Autowired
    private RouteService routeService;

    @GetMapping("")
    public ResponseEntity<?> getAll(@RequestParam(value = "page", required = false) Integer page,
                                    @RequestParam(value = "page_size", required = false) Integer pageSize,
                                    @RequestParam(value = "company_id") Long companyId,
                                    @RequestHeader HttpHeaders headers){

        try {
            return new ResponseEntity<>(
                    routeService.getAll(page, pageSize, companyId, Mapper.getUserFromHeaders(headers))
                            .stream()
                            .map(Mapper::convertToRouteDTO)
                            .collect(Collectors.toList()),
                    HttpStatus.OK
            );
        } catch (ForbiddenException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (TaskNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IncorrectDataException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable("id") Long id,
                                 @RequestHeader HttpHeaders headers){

        try {
            return new ResponseEntity<>(
                    Mapper.convertToRouteDTO(
                            routeService.get(id, Mapper.getUserFromHeaders(headers))
                    ),
                    HttpStatus.OK);
        } catch (ForbiddenException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (RouteNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("")
    public ResponseEntity<String> create(@RequestBody @Valid CreateRouteDTO routeDTO,
                                         @RequestHeader HttpHeaders headers,
                                         BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return new ResponseEntity<>(getErrorMessage(bindingResult), HttpStatus.BAD_REQUEST);
        }

        try {
            routeService.create(routeDTO, Mapper.getUserFromHeaders(headers));
            return new ResponseEntity<>("The route was successfully created", HttpStatus.CREATED);
        } catch (ForbiddenException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (TaskNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private String getErrorMessage(BindingResult bindingResult){
        StringBuilder stringBuilder = new StringBuilder();

        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            stringBuilder.append(fieldError.getField())
                    .append(" - ")
                    .append(fieldError.getDefaultMessage())
                    .append("; ");
        }

        return stringBuilder.toString();
    }
}
