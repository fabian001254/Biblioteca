package com.biblioteca.biblioteca.controller;

import com.biblioteca.biblioteca.model.User;
import com.biblioteca.biblioteca.model.Enums.Role;
import com.biblioteca.biblioteca.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class JwtAuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Usar la misma clave que SecurityConfig
    @Autowired
    private Key jwtSecretKey;

    private final long jwtExpirationMs = 86400000; // 24 horas

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        try {
            // Extraer email y password del request
            String email = loginRequest.get("email");
            String password = loginRequest.get("password");

            if (email == null || password == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email y password son requeridos");
            }

            // Buscar usuario por email
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado"));

            // Verificar contraseña
            if (!passwordEncoder.matches(password, user.getPassword())) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
            }

            // Generar token JWT
            String token = generateJwtToken(user);

            // Crear respuesta
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", user);
            response.put("role", user.getRole().name());

            return ResponseEntity.ok(response);

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error en el servidor");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> registerRequest) {
        try {
            // Extraer datos del request
            String name = registerRequest.get("name");
            String email = registerRequest.get("email");
            String password = registerRequest.get("password");

            if (name == null || email == null || password == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nombre, email y password son requeridos");
            }

            // Verificar si el usuario ya existe
            if (userRepository.findByEmail(email).isPresent()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El correo electrónico ya está en uso");
            }

            // Crear nuevo usuario (por defecto será READER)
            User newUser = new User();
            newUser.setName(name);
            newUser.setEmail(email);
            newUser.setPassword(passwordEncoder.encode(password));
            newUser.setRole(Role.READER);

            // Guardar usuario
            User savedUser = userRepository.save(newUser);

            // Generar token JWT
            String token = generateJwtToken(savedUser);

            // Crear respuesta
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", savedUser);
            response.put("role", savedUser.getRole().name());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al registrar el usuario");
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String token) {
        try {
            // Extraer el token del encabezado (eliminar "Bearer ")
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            } else {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token no válido");
            }

            // Extraer el email del token
            String email = Jwts.parserBuilder()
                    .setSigningKey(jwtSecretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();

            // Buscar el usuario
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

            return ResponseEntity.ok(user);

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token inválido o expirado");
        }
    }

    private String generateJwtToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(jwtSecretKey)
                .compact();
    }
}