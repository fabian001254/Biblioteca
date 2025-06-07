package com.biblioteca.biblioteca.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Utilidad para encriptar y verificar contraseñas usando BCrypt
 */
@Component
public class PasswordUtils {

    private final BCryptPasswordEncoder passwordEncoder;

    public PasswordUtils() {
        // Se puede configurar la fuerza del algoritmo (por defecto es 10)
        this.passwordEncoder = new BCryptPasswordEncoder(12);
    }

    /**
     * Encripta una contraseña usando BCrypt
     *
     * @param rawPassword La contraseña en texto plano
     * @return La contraseña encriptada
     */
    public String encryptPassword(String rawPassword) {
        if (rawPassword == null || rawPassword.isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía");
        }
        return passwordEncoder.encode(rawPassword);
    }

    /**
     * Verifica si una contraseña en texto plano coincide con una contraseña encriptada
     *
     * @param rawPassword La contraseña en texto plano a verificar
     * @param encodedPassword La contraseña encriptada almacenada
     * @return true si coinciden, false en caso contrario
     */
    public boolean verifyPassword(String rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) {
            return false;
        }
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}