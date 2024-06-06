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
import ru.kirill.logistService.exceptions.TaskNotFoundException;
import ru.kirill.logistService.mappers.Mapper;
import ru.kirill.logistService.models.DTOs.TaskDTO;
import ru.kirill.logistService.services.TaskService;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/task")
public class TaskController {
    @Autowired
    private TaskService taskService;

    @GetMapping("")
    public ResponseEntity<?> getAll(@RequestParam(value = "page", required = false) Integer page,
                                    @RequestParam(value = "page_size", required = false) Integer pageSize,
                                    @RequestParam(value = "company_name") String companyName,
                                    @RequestHeader HttpHeaders headers) {

        try {
            return new ResponseEntity<>(
                    taskService.getAll(page, pageSize, companyName, Mapper.getUserFromHeaders(headers)).stream()
                            .map(Mapper::convertToTaskDTO)
                            .collect(Collectors.toList()),
                    HttpStatus.OK
            );
        } catch (IncorrectDataException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (ForbiddenException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("")
    public ResponseEntity<String> createTask(@RequestBody @Valid TaskDTO taskDTO,
                                             @RequestHeader HttpHeaders headers,
                                             BindingResult bindingResult) throws JsonProcessingException {

        if(bindingResult.hasErrors()){
            return new ResponseEntity<>(getErrorMessage(bindingResult), HttpStatus.BAD_REQUEST);
        }
        try {
            taskService.create(taskDTO, Mapper.getUserFromHeaders(headers));
            return new ResponseEntity<>("The task was successfully created", HttpStatus.CREATED);
        } catch (ForbiddenException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTask(@PathVariable("id") int id,
                                           @RequestHeader HttpHeaders headers) throws JsonProcessingException {
        try {
            return new ResponseEntity<>(taskService.get(id, Mapper.getUserFromHeaders(headers)), HttpStatus.OK);
        } catch (TaskNotFoundException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (ForbiddenException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
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
