package com.biblioteca.biblioteca.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/password")
    public String testPassword() {
        String rawPassword = "password";
        String storedHash = "$2a$10$3ZP90w4a0j8AXGaoe6AaVeV4MFyQzQqYEZScB1Uqa1jCwTZ9mj2Uy";
        
        boolean matches = passwordEncoder.matches(rawPassword, storedHash);
        
        String newHash = passwordEncoder.encode(rawPassword);
        
        return "Raw Password: " + rawPassword + "<br>" +
               "Stored Hash: " + storedHash + "<br>" +
               "¿El hash almacenado coincide con 'password'? " + matches + "<br>" +
               "Nuevo hash generado: " + newHash;
    }
}
