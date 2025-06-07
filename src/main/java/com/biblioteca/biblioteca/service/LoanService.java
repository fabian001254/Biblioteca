package com.biblioteca.biblioteca.service;

import com.biblioteca.biblioteca.exceptions.bookExceptions.BookNotAvailableException;
import com.biblioteca.biblioteca.exceptions.loanExceptions.LoanAlreadyExistsException;
import com.biblioteca.biblioteca.exceptions.loanExceptions.LoanNotFoundException;
import com.biblioteca.biblioteca.mapper.BookMapper;
import com.biblioteca.biblioteca.mapper.LoanMapper;
import com.biblioteca.biblioteca.mapper.UserMapper;
import com.biblioteca.biblioteca.model.Book;
import com.biblioteca.biblioteca.model.DTO.BookDTO;
import com.biblioteca.biblioteca.model.DTO.LoanDTO;
import com.biblioteca.biblioteca.model.DTO.UserDTO;
import com.biblioteca.biblioteca.model.Enums.LoanStatus;
import com.biblioteca.biblioteca.model.Loan;
import com.biblioteca.biblioteca.model.User;
import com.biblioteca.biblioteca.repository.BookRepository;
import com.biblioteca.biblioteca.repository.LoanRepository;
import com.biblioteca.biblioteca.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LoanService {

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private BookRepository bookRepository;
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookService bookService;

    @Autowired
    private UserService userService;

    @Autowired
    private LoanMapper loanMapper;

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private UserMapper userMapper;

    public Boolean createLoan(String isbn, String email) {
        // Verificar si ya existe un préstamo activo para este libro y usuario
        Optional<Loan> existingLoan = findActiveLoanByBookAndUser(isbn, email);
        if (existingLoan.isPresent()) {
            throw new LoanAlreadyExistsException();
        }

        // Buscar el libro y el usuario
        Optional<Book> bookOpt = Optional.ofNullable(bookRepository.findByIsbn(isbn));
        if (!bookOpt.isPresent()) {
            throw new BookNotAvailableException();
        }
        
        Book book = bookOpt.get();
        if (book.getQuantity() <= 0) {
            throw new BookNotAvailableException();
        }
        
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (!userOpt.isPresent()) {
            throw new IllegalArgumentException("El usuario no existe");
        }

        // Crear el nuevo préstamo
        Loan newLoan = new Loan();
        newLoan.setBook(book);
        newLoan.setUser(userOpt.get());
        LocalDateTime now = LocalDateTime.now();
        newLoan.setLoanDate(now);
        newLoan.setDueDate(now.toLocalDate().plusDays(14));
        newLoan.setStatus(LoanStatus.ACTIVE);
        loanRepository.save(newLoan);

        // Actualizar la cantidad de libros disponibles
        book.setQuantity(book.getQuantity() - 1);
        bookRepository.save(book);
        
        return true;
    }

    public Optional<Loan> findActiveLoanByBookAndUser(String isbn, String email) {
        // Usando una consulta JPQL para encontrar préstamos activos por isbn y email
        return loanRepository.findAll().stream()
            .filter(loan -> loan.getBook() != null && loan.getUser() != null)
            .filter(loan -> loan.getBook().getIsbn().equals(isbn) && 
                   loan.getUser().getEmail().equals(email) && 
                   loan.getStatus() == LoanStatus.ACTIVE)
            .findFirst();
    }

    public Optional<Loan> findLoanById(Long id) {
        return loanRepository.findById(id);
    }

    public List<Loan> findAllActiveLoans() {
        return loanRepository.findByStatus(LoanStatus.ACTIVE);
    }

    public List<Loan> findOverdueLoans() {
        LocalDate today = LocalDate.now();
        // Buscar préstamos activos con fecha de devolución anterior a hoy
        return loanRepository.findAll().stream()
            .filter(loan -> loan.getStatus() == LoanStatus.ACTIVE && 
                   loan.getDueDate() != null && 
                   loan.getDueDate().isBefore(today))
            .collect(Collectors.toList());
    }

    public List<Loan> findLoansByUser(String email) {
        // Buscar todos los préstamos de un usuario por email
        return loanRepository.findAll().stream()
            .filter(loan -> loan.getUser() != null && 
                   loan.getUser().getEmail().equals(email))
            .collect(Collectors.toList());
    }

    public List<Loan> findActiveLoansForUser(String email) {
        // Buscar préstamos activos de un usuario por email
        return loanRepository.findAll().stream()
            .filter(loan -> loan.getUser() != null && 
                   loan.getUser().getEmail().equals(email) && 
                   loan.getStatus() == LoanStatus.ACTIVE)
            .collect(Collectors.toList());
    }

    public boolean returnBook(String isbn, String email) {
        Optional<Loan> loanOpt = findActiveLoanByBookAndUser(isbn, email);
        if (!loanOpt.isPresent()) {
            throw new LoanNotFoundException();
        }

        Loan loan = loanOpt.get();
        loan.setReturnDate(LocalDateTime.now());
        loan.setStatus(LoanStatus.RETURNED);
        loanRepository.save(loan);

        // Incrementar la cantidad de libros disponibles
        Book book = loan.getBook();
        book.setQuantity(book.getQuantity() + 1);
        bookRepository.save(book);

        return true;
    }

    public boolean isBookBorrowed(String isbn) {
        // Verificar si hay préstamos activos para este libro
        return loanRepository.findAll().stream()
            .anyMatch(loan -> loan.getBook() != null && 
                     loan.getBook().getIsbn().equals(isbn) && 
                     loan.getStatus() == LoanStatus.ACTIVE);
    }

    public boolean hasUserBorrowedBooks(String email) {
        // Verificar si el usuario tiene préstamos activos
        return loanRepository.findAll().stream()
            .anyMatch(loan -> loan.getUser() != null && 
                     loan.getUser().getEmail().equals(email) && 
                     loan.getStatus() == LoanStatus.ACTIVE);
    }

    public int countActiveLoansByUser(String email) {
        // Contar los préstamos activos de un usuario
        return (int) loanRepository.findAll().stream()
            .filter(loan -> loan.getUser() != null && 
                   loan.getUser().getEmail().equals(email) && 
                   loan.getStatus() == LoanStatus.ACTIVE)
            .count();
    }

    public boolean renewLoan(Long loanId) {
        Optional<Loan> loanOpt = loanRepository.findById(loanId);
        if (!loanOpt.isPresent()) {
            throw new LoanNotFoundException();
        }

        Loan loan = loanOpt.get();
        if (loan.getStatus() != LoanStatus.ACTIVE) {
            throw new IllegalStateException("No se puede renovar un préstamo que no está activo");
        }

        if (loan.getDueDate().isBefore(LocalDate.now())) {
            throw new IllegalStateException("No se puede renovar un préstamo vencido");
        }

        loan.setDueDate(loan.getDueDate().plusDays(14));
        loanRepository.save(loan);

        return true;
    }

    public boolean markAsLost(Long loanId) {
        Optional<Loan> loanOpt = loanRepository.findById(loanId);
        if (!loanOpt.isPresent()) {
            throw new LoanNotFoundException();
        }

        Loan loan = loanOpt.get();
        if (loan.getStatus() != LoanStatus.ACTIVE) {
            throw new IllegalStateException("Solo los préstamos activos pueden marcarse como perdidos");
        }

        loan.setStatus(LoanStatus.LOST);
        loanRepository.save(loan);

        return true;
    }
}