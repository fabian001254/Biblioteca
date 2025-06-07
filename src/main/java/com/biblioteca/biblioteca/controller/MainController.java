package com.biblioteca.biblioteca.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

    @GetMapping("/api")
    public String home() {
        return "Bienvenido a la API de la Biblioteca";
    }
}
