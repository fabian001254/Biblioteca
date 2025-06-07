package com.biblioteca.biblioteca.controller;

import java.util.stream.Collectors;

import com.biblioteca.biblioteca.model.Enums.Role;
import com.biblioteca.biblioteca.model.User;
import com.biblioteca.biblioteca.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Endpoint para obtener todos los lectores (solo para bibliotecarios y administradores)
    @GetMapping("/readers")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    public ResponseEntity<List<User>> getAllReaders() {
        List<User> readers = userRepository.findByRole(Role.READER);
        return ResponseEntity.ok(readers);
    }

    // Endpoint para obtener un lector por ID (solo para bibliotecarios y administradores)
    @GetMapping("/readers/{id}")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    public ResponseEntity<User> getReaderById(@PathVariable Long id) {
        Optional<User> reader = userRepository.findById(id);
        if (reader.isPresent() && reader.get().getRole() == Role.READER) {
            return ResponseEntity.ok(reader.get());
        }
        return ResponseEntity.notFound().build();
    }

    // Endpoint para crear un nuevo lector (solo para bibliotecarios y administradores)
    @PostMapping("/readers")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    public ResponseEntity<User> createReader(@RequestBody Map<String, String> request) {
        try {
            // Verificar si el correo ya existe
            String email = request.get("email");
            if (userRepository.findByEmail(email).isPresent()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El correo electrónico ya está en uso");
            }

            // Crear el nuevo usuario con rol READER
            User newUser = new User();
            newUser.setName(request.get("name"));
            newUser.setSurname(request.getOrDefault("surname", ""));
            newUser.setEmail(email);
            newUser.setPassword(passwordEncoder.encode(request.get("password")));
            newUser.setPhone(request.getOrDefault("phone", ""));
            newUser.setAddress(request.getOrDefault("address", ""));
            newUser.setCity(request.getOrDefault("city", ""));
            newUser.setRole(Role.READER);

            User savedUser = userRepository.save(newUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al crear el lector");
        }
    }

    // Endpoint para actualizar un lector (solo para bibliotecarios y administradores)
    @PutMapping("/readers/{id}")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    public ResponseEntity<User> updateReader(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            Optional<User> existingUserOpt = userRepository.findById(id);
            if (!existingUserOpt.isPresent() || existingUserOpt.get().getRole() != Role.READER) {
                return ResponseEntity.notFound().build();
            }

            User existingUser = existingUserOpt.get();

            // Actualizar los campos del usuario
            if (request.containsKey("name")) {
                existingUser.setName(request.get("name"));
            }
            if (request.containsKey("surname")) {
                existingUser.setSurname(request.get("surname"));
            }
            if (request.containsKey("email")) {
                String newEmail = request.get("email");
                if (!newEmail.equals(existingUser.getEmail()) && 
                    userRepository.findByEmail(newEmail).isPresent()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El correo electrónico ya está en uso");
                }
                existingUser.setEmail(newEmail);
            }
            if (request.containsKey("password")) {
                existingUser.setPassword(passwordEncoder.encode(request.get("password")));
            }
            if (request.containsKey("phone")) {
                existingUser.setPhone(request.get("phone"));
            }
            if (request.containsKey("address")) {
                existingUser.setAddress(request.get("address"));
            }
            if (request.containsKey("city")) {
                existingUser.setCity(request.get("city"));
            }

            User updatedUser = userRepository.save(existingUser);
            return ResponseEntity.ok(updatedUser);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al actualizar el lector");
        }
    }

    // Endpoint para eliminar un lector (solo para bibliotecarios y administradores)
    @DeleteMapping("/readers/{id}")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    public ResponseEntity<Void> deleteReader(@PathVariable Long id) {
        try {
            Optional<User> userOpt = userRepository.findById(id);
            if (!userOpt.isPresent() || userOpt.get().getRole() != Role.READER) {
                return ResponseEntity.notFound().build();
            }

            // Aquí se podría verificar si el lector tiene préstamos activos antes de eliminarlo
            // Por simplicidad, lo eliminamos directamente
            userRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al eliminar el lector");
        }
    }

    // Endpoint para buscar lectores por nombre o email (solo para bibliotecarios y administradores)
    @GetMapping("/readers/search")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    public ResponseEntity<List<User>> searchReaders(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email) {
        
        List<User> readers;
        
        if (name != null && !name.isEmpty() && email != null && !email.isEmpty()) {
            // Buscar por nombre y email
            readers = userRepository.findAll().stream()
                .filter(user -> user.getRole() == Role.READER)
                .filter(user -> user.getName().toLowerCase().contains(name.toLowerCase()) &&
                               user.getEmail().toLowerCase().contains(email.toLowerCase()))
                .collect(Collectors.toList());
        } else if (name != null && !name.isEmpty()) {
            // Buscar solo por nombre
            readers = userRepository.findAll().stream()
                .filter(user -> user.getRole() == Role.READER)
                .filter(user -> user.getName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
        } else if (email != null && !email.isEmpty()) {
            // Buscar solo por email
            readers = userRepository.findAll().stream()
                .filter(user -> user.getRole() == Role.READER)
                .filter(user -> user.getEmail().toLowerCase().contains(email.toLowerCase()))
                .collect(Collectors.toList());
        } else {
            // Si no hay parámetros, devolver todos los lectores
            readers = userRepository.findByRole(Role.READER);
        }
        
        return ResponseEntity.ok(readers);
    }
}
