package com.biblioteca.biblioteca.model;

import com.biblioteca.biblioteca.model.Enums.LoanStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class LoanTest {

    private Loan loan;
    private final Long TEST_ID = 1L;
    private final LocalDateTime TEST_LOAN_DATE = LocalDateTime.now();
    private final LocalDate TEST_DUE_DATE = LocalDate.now().plusDays(14);
    private final LocalDateTime TEST_RETURN_DATE = null;
    private final LoanStatus TEST_STATUS = LoanStatus.ACTIVE;
    private final String TEST_NOTES = "Test loan";
    
    private User testUser;
    private Book testBook;

    @BeforeEach
    void setUp() {
        // Crear usuario de prueba
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        
        // Crear libro de prueba
        testBook = new Book();
        testBook.setId(1L);
        testBook.setTitle("Test Book");
        testBook.setIsbn("1234567890");
        
        // Crear préstamo de prueba
        loan = new Loan();
        loan.setId(TEST_ID);
        loan.setBook(testBook);
        loan.setUser(testUser);
        loan.setLoanDate(TEST_LOAN_DATE);
        loan.setDueDate(TEST_DUE_DATE);
        loan.setReturnDate(TEST_RETURN_DATE);
        loan.setStatus(TEST_STATUS);
        loan.setNotes(TEST_NOTES);
    }

    @Test
    void testLoanCreation() {
        assertNotNull(loan);
        assertEquals(TEST_ID, loan.getId());
        assertEquals(testBook, loan.getBook());
        assertEquals(testUser, loan.getUser());
        assertEquals(TEST_LOAN_DATE, loan.getLoanDate());
        assertEquals(TEST_DUE_DATE, loan.getDueDate());
        assertEquals(TEST_RETURN_DATE, loan.getReturnDate());
        assertEquals(TEST_STATUS, loan.getStatus());
        assertEquals(TEST_NOTES, loan.getNotes());
    }

    @Test
    void testLoanSetters() {
        // Crear nuevos objetos para probar los setters
        User newUser = new User();
        newUser.setId(2L);
        newUser.setName("New User");
        
        Book newBook = new Book();
        newBook.setId(2L);
        newBook.setTitle("New Book");
        
        LocalDateTime newLoanDate = LocalDateTime.now().minusDays(1);
        LocalDate newDueDate = LocalDate.now().plusDays(7);
        LocalDateTime newReturnDate = LocalDateTime.now();
        LoanStatus newStatus = LoanStatus.RETURNED;
        String newNotes = "Updated notes";
        
        loan.setBook(newBook);
        loan.setUser(newUser);
        loan.setLoanDate(newLoanDate);
        loan.setDueDate(newDueDate);
        loan.setReturnDate(newReturnDate);
        loan.setStatus(newStatus);
        loan.setNotes(newNotes);
        
        assertEquals(newBook, loan.getBook());
        assertEquals(newUser, loan.getUser());
        assertEquals(newLoanDate, loan.getLoanDate());
        assertEquals(newDueDate, loan.getDueDate());
        assertEquals(newReturnDate, loan.getReturnDate());
        assertEquals(newStatus, loan.getStatus());
        assertEquals(newNotes, loan.getNotes());
    }

    @Test
    void testLoanAllArgsConstructor() {
        Loan fullLoan = new Loan(
            TEST_ID, 
            testBook, 
            testUser, 
            TEST_LOAN_DATE, 
            TEST_DUE_DATE, 
            TEST_RETURN_DATE, 
            TEST_STATUS, 
            TEST_NOTES
        );
        
        assertNotNull(fullLoan);
        assertEquals(TEST_ID, fullLoan.getId());
        assertEquals(testBook, fullLoan.getBook());
        assertEquals(testUser, fullLoan.getUser());
        assertEquals(TEST_LOAN_DATE, fullLoan.getLoanDate());
        assertEquals(TEST_DUE_DATE, fullLoan.getDueDate());
        assertEquals(TEST_RETURN_DATE, fullLoan.getReturnDate());
        assertEquals(TEST_STATUS, fullLoan.getStatus());
        assertEquals(TEST_NOTES, fullLoan.getNotes());
    }
    
    @Test
    void testLoanStatus() {
        // Probar la enumeración de estados
        assertEquals("ACTIVE", LoanStatus.ACTIVE.toString());
        assertEquals("RETURNED", LoanStatus.RETURNED.toString());
        assertEquals("OVERDUE", LoanStatus.OVERDUE.toString());
        assertEquals("LOST", LoanStatus.LOST.toString());
    }
}
