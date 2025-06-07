package com.biblioteca.biblioteca.mapper;

import com.biblioteca.biblioteca.exceptions.bookExceptions.BookNotExist;
import com.biblioteca.biblioteca.exceptions.userExceptions.UserNotExist;
import com.biblioteca.biblioteca.model.Book;
import com.biblioteca.biblioteca.model.DTO.LoanDTO;
import com.biblioteca.biblioteca.model.Loan;
import com.biblioteca.biblioteca.model.User;
import com.biblioteca.biblioteca.repository.BookRepository;
import com.biblioteca.biblioteca.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class LoanMapper {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    @Autowired
    public LoanMapper(BookRepository bookRepository, UserRepository userRepository) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    public LoanDTO loanToLoanDTO(Loan loan) {
        return new LoanDTO(
                loan.getId(),
                loan.getBook().getId(),
                loan.getBook().getTitle(),
                loan.getUser().getId(),
                loan.getUser().getEmail(),
                loan.getLoanDate(),
                loan.getDueDate(),
                loan.getReturnDate(),
                loan.getStatus(),
                loan.getNotes()
        );
    }

    public Loan loanDTOToLoan(LoanDTO loanDTO) {
        Loan loan = new Loan();

        loan.setId(loanDTO.getId());
        loan.setLoanDate(loanDTO.getLoanDate());
        loan.setDueDate(loanDTO.getDueDate());
        loan.setReturnDate(loanDTO.getReturnDate());
        loan.setStatus(loanDTO.getStatus());
        loan.setNotes(loanDTO.getNotes());

        if (loanDTO.getBookId() != null) {
            Book book = bookRepository.findById(loanDTO.getBookId())
                    .orElseThrow(BookNotExist::new);
            loan.setBook(book);
        }

        if (loanDTO.getUserId() != null) {
            User user = userRepository.findById((long) Math.toIntExact(loanDTO.getUserId()))
                    .orElseThrow(UserNotExist::new);
            loan.setUser(user);
        }

        return loan;
    }
}