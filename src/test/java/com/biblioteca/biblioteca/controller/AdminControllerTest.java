package com.biblioteca.biblioteca.controller;

import com.biblioteca.biblioteca.model.User;
import com.biblioteca.biblioteca.model.Enums.Role;
import com.biblioteca.biblioteca.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AdminControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdminController adminController;

    private User testLibrarian;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        testLibrarian = new User();
        testLibrarian.setId(1L);
        testLibrarian.setName("Test");
        testLibrarian.setEmail("librarian@example.com");
        testLibrarian.setPassword("encodedPassword");
        testLibrarian.setRole(Role.LIBRARIAN);
    }

    @Test
    void createLibrarian_Success() {
        // Arrange
        Map<String, String> librarianMap = new java.util.HashMap<>();
        librarianMap.put("name", "New Librarian");
        librarianMap.put("email", "new@example.com");
        librarianMap.put("password", "password");
        librarianMap.put("surname", "");
        librarianMap.put("phone", "");
        librarianMap.put("address", "");
        librarianMap.put("city", "");

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testLibrarian);

        // Act
        ResponseEntity<User> response = adminController.createLibrarian(librarianMap);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(Role.LIBRARIAN, response.getBody().getRole());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void getAllLibrarians_Success() {
        // Arrange
        List<User> librarians = Arrays.asList(testLibrarian);
        when(userRepository.findByRole(Role.LIBRARIAN)).thenReturn(librarians);

        // Act
        List<User> result = adminController.getAllLibrarians();

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("librarian@example.com", result.get(0).getEmail());
    }

    @Test
    void deleteLibrarian_Success() {
        // Arrange
        Long librarianId = 1L;
        doNothing().when(userRepository).deleteById(librarianId);

        // Act
        ResponseEntity<Void> response = adminController.deleteLibrarian(librarianId);

        // Assert
        assertEquals(204, response.getStatusCodeValue());
        verify(userRepository, times(1)).deleteById(librarianId);
    }
}
