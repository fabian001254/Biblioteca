package com.biblioteca.biblioteca.controller;

import com.biblioteca.biblioteca.model.Loan;
import com.biblioteca.biblioteca.model.User;
import com.biblioteca.biblioteca.model.Enums.LoanStatus;
import com.biblioteca.biblioteca.model.Enums.Role;
import com.biblioteca.biblioteca.repository.LoanRepository;
import com.biblioteca.biblioteca.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class LibrarianControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private LibrarianController librarianController;

    private User testReader;
    private Loan testLoan;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Configurar lector de prueba
        testReader = new User();
        testReader.setId(1L);
        testReader.setName("Test Reader");
        testReader.setEmail("reader@example.com");
        testReader.setRole(Role.READER);

        // Configurar préstamo de prueba
        testLoan = new Loan();
        testLoan.setId(1L);
        testLoan.setUser(testReader);
        testLoan.setStatus(LoanStatus.ACTIVE);
    }

    @Test
    void createReader_Success() {
        // Arrange
        Map<String, String> userMap = new java.util.HashMap<>();
        userMap.put("name", "New Reader");
        userMap.put("surname", "");
        userMap.put("email", "newreader@example.com");
        userMap.put("password", "somepassword");
        userMap.put("phone", "");
        userMap.put("address", "");
        userMap.put("city", "");

        when(userRepository.save(any(User.class))).thenReturn(testReader);

        // Act
        ResponseEntity<User> response = librarianController.createReader(userMap);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(Role.READER, response.getBody().getRole());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void getAllReaders_Success() {
        // Arrange
        List<User> readers = Arrays.asList(testReader);
        when(userRepository.findByRole(Role.READER)).thenReturn(readers);

        // Act
        List<User> result = librarianController.getAllReaders();

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("reader@example.com", result.get(0).getEmail());
    }

    @Test
    void createLoan_Success() {
        // Arrange
        Loan newLoan = new Loan();
        newLoan.setUser(testReader);
        newLoan.setStatus(LoanStatus.ACTIVE);

        when(loanRepository.save(any(Loan.class))).thenReturn(testLoan);

        // Act
        ResponseEntity<Loan> response = librarianController.createLoan(newLoan);


        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(LoanStatus.ACTIVE, response.getBody().getStatus());
        verify(loanRepository, times(1)).save(any(Loan.class));
    }

    @Test
    void returnBook_Success() {
        // Arrange
        Long loanId = 1L;
        when(loanRepository.findById(loanId)).thenReturn(Optional.of(testLoan));
        when(loanRepository.save(any(Loan.class))).thenReturn(testLoan);

        // Act
        ResponseEntity<Loan> response = librarianController.returnBook(loanId);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(LoanStatus.RETURNED, response.getBody().getStatus());
        assertNotNull(response.getBody().getReturnDate());
    }

    @Test
    void createLoanWithMap_Success() {
        // Arrange
        Map<String, Object> loanData = new java.util.HashMap<>();
        loanData.put("bookId", 1L);
        loanData.put("userId", 1L);
        loanData.put("dueDate", "2025-06-21");
        loanData.put("notes", "Test loan");

        when(loanRepository.save(any(Loan.class))).thenReturn(testLoan);

        // Act
        ResponseEntity<Loan> response = librarianController.createLoan(loanData);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(LoanStatus.ACTIVE, response.getBody().getStatus());
        verify(loanRepository, times(1)).save(any(Loan.class));
    }

    @Test
    void createLoanWithMap_BadRequest() {
        // Arrange: datos incompletos para provocar error
        Map<String, Object> loanData = new java.util.HashMap<>();
        loanData.put("userId", 1L); // Falta bookId
        // No dueDate ni notes

        // Act
        ResponseEntity<Loan> response = librarianController.createLoan(loanData);

        // Assert
        assertEquals(400, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void createLoanWithLoan_BadRequest() {
        // Arrange
        Loan newLoan = new Loan();
        newLoan.setUser(testReader);
        newLoan.setStatus(LoanStatus.ACTIVE);
        // Forzamos excepción al guardar
        when(loanRepository.save(any(Loan.class))).thenThrow(new RuntimeException("DB error"));

        // Act
        ResponseEntity<Loan> response = librarianController.createLoan(newLoan);

        // Assert
        assertEquals(400, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void getActiveLoans_Success() {
        // Arrange
        List<Loan> activeLoans = Arrays.asList(testLoan);
        when(loanRepository.findByStatus(LoanStatus.ACTIVE)).thenReturn(activeLoans);

        // Act
        List<Loan> result = librarianController.getActiveLoans();

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(LoanStatus.ACTIVE, result.get(0).getStatus());
    }
}
