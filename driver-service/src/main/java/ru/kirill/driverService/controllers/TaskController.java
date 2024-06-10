package ru.kirill.driverService.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kirill.driverService.clients.LogistClient;

@RestController
@RequestMapping("/task")
public class TaskController {

    @Autowired
    private LogistClient logistClient;

    @GetMapping("")
    public ResponseEntity<?> getTask(@RequestHeader HttpHeaders headers){
        return new ResponseEntity<>(logistClient.findDriverTask(headers.toSingleValueMap()), HttpStatus.OK);
    }

}
