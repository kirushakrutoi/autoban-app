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
import ru.kirill.models.DTOs.CreatePointDTO;

@RestController
@RequestMapping("/point")
public class PointController {
    @Autowired
    private KafkaProducer kafkaProducer;

    @PostMapping("")
    public HttpStatus pointMessage(@RequestBody CreatePointDTO pointDTO) {
        try {
            kafkaProducer.sendPointMessage(pointDTO);
            return HttpStatus.OK;
        } catch (JsonProcessingException e) {
            return HttpStatus.BAD_REQUEST;
        }

    }
}
