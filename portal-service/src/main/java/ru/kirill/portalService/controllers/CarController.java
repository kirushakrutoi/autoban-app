package ru.kirill.portalService.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kirill.portalService.exceptions.carexceptions.CarAlreadyExistException;
import ru.kirill.portalService.exceptions.carexceptions.CarNotFoundExceptions;
import ru.kirill.portalService.exceptions.userexception.ForbiddenException;
import ru.kirill.portalService.mappers.Mapper;
import ru.kirill.portalService.model.Car;
import ru.kirill.portalService.model.DTOs.GetCompanyDTO;
import ru.kirill.portalService.services.CarService;

@RestController
@RequestMapping("/car")
public class CarController {
    @Autowired
    private CarService carService;

    @PostMapping("/add")
    public ResponseEntity<String> addCar(@RequestBody Car car,
                                             @RequestHeader HttpHeaders headers) throws JsonProcessingException {
        try {
            carService.addCar(car, Mapper.getUserFromHeaders(headers));
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (CarAlreadyExistException | ForbiddenException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

//    @GetMapping("/{id}")
//    public ResponseEntity<?> getCar(@PathVariable("id") long id,
//                                    @RequestHeader HttpHeaders headers,
//                                    @RequestBody GetCompanyDTO companyDTO){
//
//        try {
//            return new ResponseEntity<>(carService.getCar(id, Mapper.getUserFromHeaders(headers), companyDTO.getName()), HttpStatus.OK);
//        } catch (ForbiddenException e) {
//            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
//        } catch (CarNotFoundExceptions e) {
//            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }
//    }

    @PostMapping("/{id}")
    public ResponseEntity<?> getCar(@PathVariable("id") long id,
                                    @RequestHeader HttpHeaders headers,
                                    @RequestBody GetCompanyDTO companyDTO){

        try {
            return new ResponseEntity<>(carService.getCar(id, Mapper.getUserFromHeaders(headers), companyDTO.getName()), HttpStatus.OK);
        } catch (ForbiddenException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (CarNotFoundExceptions e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
