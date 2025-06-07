package com.biblioteca.biblioteca.model;

import com.biblioteca.biblioteca.model.Enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    private User user;
    private final Long TEST_ID = 1L;
    private final String TEST_NAME = "Test User";
    private final String TEST_EMAIL = "test@example.com";
    private final Role TEST_ROLE = Role.READER;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(TEST_ID);
        user.setName(TEST_NAME);
        user.setEmail(TEST_EMAIL);
        user.setRole(TEST_ROLE);
    }

    @Test
    void testUserCreation() {
        assertNotNull(user);
        assertEquals(TEST_ID, user.getId());
        assertEquals(TEST_NAME, user.getName());
        assertEquals(TEST_EMAIL, user.getEmail());
        assertEquals(TEST_ROLE, user.getRole());
    }

    @Test
    void testUserSetters() {
        String newName = "Updated User";
        String newEmail = "updated@example.com";
        Role newRole = Role.LIBRARIAN;
        
        user.setName(newName);
        user.setEmail(newEmail);
        user.setRole(newRole);
        
        assertEquals(newName, user.getName());
        assertEquals(newEmail, user.getEmail());
        assertEquals(newRole, user.getRole());
    }

    @Test
    void testUserAllArgsConstructor() {
        String surname = "Test Surname";
        String password = "password123";
        String phone = "123456789";
        String address = "Test Address";
        String city = "Test City";
        
        User fullUser = new User(TEST_ID, TEST_NAME, surname, TEST_EMAIL, password, phone, address, city, TEST_ROLE);
        
        assertNotNull(fullUser);
        assertEquals(TEST_ID, fullUser.getId());
        assertEquals(TEST_NAME, fullUser.getName());
        assertEquals(surname, fullUser.getSurname());
        assertEquals(TEST_EMAIL, fullUser.getEmail());
        assertEquals(password, fullUser.getPassword());
        assertEquals(phone, fullUser.getPhone());
        assertEquals(address, fullUser.getAddress());
        assertEquals(city, fullUser.getCity());
        assertEquals(TEST_ROLE, fullUser.getRole());
    }
}
