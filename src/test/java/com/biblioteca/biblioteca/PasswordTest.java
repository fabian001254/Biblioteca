package com.biblioteca.biblioteca;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PasswordTest {
    
    @Test
    public void testPasswordHash() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "password";
        String hash = encoder.encode(rawPassword);
        assertTrue(encoder.matches(rawPassword, hash), "El password debería coincidir con el hash generado");
    }
}
