package ru.kirill.portalService.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kirill.portalService.mappers.Mapper;
import ru.kirill.portalService.model.Car;
import ru.kirill.portalService.services.CarService;

@RestController
@RequestMapping("/car")
public class CarController {
    @Autowired
    private CarService carService;

    @PostMapping("/add")
    public ResponseEntity<HttpStatus> addCar(@RequestBody Car car,
                                             @RequestHeader HttpHeaders headers) throws JsonProcessingException {
        carService.addCar(car, Mapper.getUserFromHeaders(headers));
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
