package ru.kirill.driverService.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kirill.driverService.kafka.KafkaProducer;
import ru.kirill.exceptions.StatusNotExistException;
import ru.kirill.models.DTOs.EventDTO;

@RestController
@RequestMapping("/event")
public class EventController {
    @Autowired
    private KafkaProducer kafkaProducer;

    @PostMapping("")
    public ResponseEntity<String> eventMessage(@RequestBody EventDTO eventDTO) throws JsonProcessingException {
        try {
            kafkaProducer.sendEventMessage(eventDTO);
            return new ResponseEntity<>("The event was successfully sent", HttpStatus.OK);
        } catch (StatusNotExistException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
