package ru.kirill.dwhService.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("hello")
public class Controller {
    @GetMapping
    public String hello(){
        return "hello from dwh service";
    }
}
