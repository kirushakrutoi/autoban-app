package ru.kirill.dwhService.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kirill.dwhService.exceptions.CompanyNotFoundException;
import ru.kirill.dwhService.exceptions.ForbiddenException;
import ru.kirill.dwhService.services.StatService;
import ru.kirill.mappers.UserMapper;
import ru.kirill.models.User;

@RestController
@RequestMapping("/stat")
public class StatController {

    @Autowired
    private StatService statService;

    @GetMapping("")
    public ResponseEntity<?> getStat(@RequestParam("company_name") String companyName,
                                     @RequestHeader HttpHeaders headers) throws JsonProcessingException {

        try {
            return new ResponseEntity<>(statService.getStat(companyName, UserMapper.getUserFromHeaders(headers)), HttpStatus.OK);
        } catch (ForbiddenException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (CompanyNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
