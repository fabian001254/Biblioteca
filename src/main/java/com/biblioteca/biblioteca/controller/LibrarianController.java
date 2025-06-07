package com.biblioteca.biblioteca.controller;

import com.biblioteca.biblioteca.model.Loan;
import com.biblioteca.biblioteca.model.User;
import com.biblioteca.biblioteca.model.Enums.LoanStatus;
import com.biblioteca.biblioteca.model.Enums.Role;
import com.biblioteca.biblioteca.repository.LoanRepository;
import com.biblioteca.biblioteca.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/librarian")
public class LibrarianController {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private LoanRepository loanRepository;
    
    // Crear un nuevo usuario (lector)
    @PostMapping("/readers")
    public ResponseEntity<User> createReader(@RequestBody Map<String, String> request) {
        User user = new User();
        user.setName(request.get("name"));
        user.setSurname(request.getOrDefault("surname", ""));
        user.setEmail(request.get("email"));
        user.setPassword(request.get("password"));
        user.setPhone(request.getOrDefault("phone", ""));
        user.setAddress(request.getOrDefault("address", ""));
        user.setCity(request.getOrDefault("city", ""));
        user.setRole(Role.READER);
        
        User savedUser = userRepository.save(user);
        return ResponseEntity.ok(savedUser);
    }
    
    // Listar todos los lectores
    @GetMapping("/readers")
    public List<User> getAllReaders() {
        return userRepository.findByRole(Role.READER);
    }
    
    // Crear un nuevo préstamo desde un objeto Loan (para pruebas o uso directo)
    @PostMapping("/loans/direct")
    public ResponseEntity<Loan> createLoan(@RequestBody Loan loan) {
        try {
            loan.setLoanDate(LocalDateTime.now());
            loan.setStatus(LoanStatus.ACTIVE);
            Loan savedLoan = loanRepository.save(loan);
            return ResponseEntity.ok(savedLoan);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Crear un nuevo préstamo
    @PostMapping("/loans")
    public ResponseEntity<Loan> createLoan(@RequestBody Map<String, Object> request) {
        try {
            // Extraer datos del request
            Long bookId = Long.valueOf(request.get("bookId").toString());
            Long userId = Long.valueOf(request.get("userId").toString());
            String dueDateStr = (String) request.get("dueDate");
            LocalDate dueDate = dueDateStr != null ? LocalDate.parse(dueDateStr) : LocalDate.now().plusDays(14);
            String notes = (String) request.getOrDefault("notes", "");
            
            // Crear el préstamo
            Loan loan = new Loan();
            loan.setLoanDate(LocalDateTime.now());
            loan.setDueDate(dueDate);
            loan.setStatus(LoanStatus.ACTIVE);
            loan.setNotes(notes);
            
            // Buscar libro y usuario
            // Nota: En una aplicación real, deberíamos verificar si el libro está disponible
            Loan savedLoan = loanRepository.save(loan);
            return ResponseEntity.ok(savedLoan);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Devolver un libro
    @PutMapping("/loans/{id}/return")
    public ResponseEntity<Loan> returnBook(@PathVariable Long id) {
        Optional<Loan> optionalLoan = loanRepository.findById(id);
        if (optionalLoan.isPresent()) {
            Loan loan = optionalLoan.get();
            loan.setReturnDate(LocalDateTime.now());
            loan.setStatus(LoanStatus.RETURNED);
            Loan updatedLoan = loanRepository.save(loan);
            return ResponseEntity.ok(updatedLoan);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Listar préstamos activos
    @GetMapping("/loans/active")
    public List<Loan> getActiveLoans() {
        return loanRepository.findByStatus(LoanStatus.ACTIVE);
    }
}
