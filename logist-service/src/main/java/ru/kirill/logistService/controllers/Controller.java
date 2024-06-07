package ru.kirill.logistService.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import feign.Feign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kirill.logistService.kafka.KafkaProducer;
import ru.kirill.logistService.models.DTOs.*;
import ru.kirill.logistService.services.PortalClient;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/hello")
public class Controller {
    @Autowired
    private KafkaProducer kafkaProducer;
    @Autowired
    private PortalClient portalClient;
    @GetMapping
    public String hello(){
        return "hello from logist service";
    }

    @PostMapping("/event")
    public String eventMessage(@RequestBody EventDTO eventDTO) throws JsonProcessingException {
        System.out.println(eventDTO.getEventTime());
        kafkaProducer.sendEventMessage(eventDTO);
/*        ObjectMapper objectMapper = new ObjectMapper();
        //objectMapper.registerModule(new JavaTimeModule());
        LocalDateTime localDateTime = LocalDateTime.parse(eventDTO.getEventTime());
        System.out.println(localDateTime);*/
        return "OK";
    }

    @PostMapping("/point")
    public String pointMessage(@RequestBody CreatePointDTO pointDTO) throws JsonProcessingException {
        System.out.println(pointDTO.getRouteId());
        kafkaProducer.sendPointMessage(pointDTO);
        return "OK";
    }

    @GetMapping("/car/{id}")
    public ResponseEntity<CarDTO> getCar(@PathVariable("id") long id,
                                         @RequestHeader HttpHeaders headers,
                                         @RequestBody CompanyDTO companyDTO){
        return new ResponseEntity<>(portalClient.findCarById(id, headers.toSingleValueMap(), companyDTO), HttpStatus.OK);
    }
}
