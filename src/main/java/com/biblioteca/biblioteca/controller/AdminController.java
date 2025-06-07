package com.biblioteca.biblioteca.controller;

import com.biblioteca.biblioteca.model.User;
import com.biblioteca.biblioteca.model.Enums.Role;
import com.biblioteca.biblioteca.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    // Crear un nuevo bibliotecario
    @PostMapping("/librarians")
    public ResponseEntity<User> createLibrarian(@RequestBody Map<String, String> request) {
        User user = new User();
        user.setName(request.get("name"));
        user.setSurname(request.getOrDefault("surname", ""));
        user.setEmail(request.get("email"));
        user.setPassword(passwordEncoder.encode(request.get("password")));
        user.setPhone(request.getOrDefault("phone", ""));
        user.setAddress(request.getOrDefault("address", ""));
        user.setCity(request.getOrDefault("city", ""));
        user.setRole(Role.LIBRARIAN);
        
        User savedUser = userRepository.save(user);
        return ResponseEntity.ok(savedUser);
    }
    
    // Listar todos los bibliotecarios
    @GetMapping("/librarians")
    public List<User> getAllLibrarians() {
        return userRepository.findByRole(Role.LIBRARIAN);
    }
    
    // Eliminar un bibliotecario
    @DeleteMapping("/librarians/{id}")
    public ResponseEntity<Void> deleteLibrarian(@PathVariable Long id) {
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
