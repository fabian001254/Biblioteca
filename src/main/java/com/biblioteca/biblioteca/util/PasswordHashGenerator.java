package com.biblioteca.biblioteca.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "password";
        String encodedPassword = encoder.encode(rawPassword);
        
        System.out.println("Raw Password: " + rawPassword);
        System.out.println("Encoded Password: " + encodedPassword);
        
        // Verificar si el hash en data.sql coincide con "password"
        String storedHash = "$2a$10$3ZP90w4a0j8AXGaoe6AaVeV4MFyQzQqYEZScB1Uqa1jCwTZ9mj2Uy";
        boolean matches = encoder.matches(rawPassword, storedHash);
        System.out.println("¿El hash almacenado coincide con 'password'? " + matches);
    }
}
