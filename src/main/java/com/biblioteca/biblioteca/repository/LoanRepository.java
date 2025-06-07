package com.biblioteca.biblioteca.repository;

import com.biblioteca.biblioteca.model.Loan;
import com.biblioteca.biblioteca.model.Enums.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByStatus(LoanStatus status);
    List<Loan> findByUserId(Long userId);
    Optional<Loan> findByIdAndStatus(Long id, LoanStatus status);
    long countByUserIdAndStatus(Long userId, LoanStatus status);
    long countByStatus(LoanStatus status);
    
    @Query("SELECT l FROM Loan l WHERE l.user.email LIKE %?1%")
    List<Loan> findByUserEmailContaining(String email);
    
    @Query("SELECT l FROM Loan l WHERE l.book.title LIKE %?1%")
    List<Loan> findByBookTitleContaining(String title);
    
    @Query("SELECT l FROM Loan l WHERE l.status = ?1 AND l.user.email LIKE %?2%")
    List<Loan> findByStatusAndUserEmailContaining(LoanStatus status, String email);
    
    @Query("SELECT l FROM Loan l WHERE l.status = ?1 AND l.book.title LIKE %?2%")
    List<Loan> findByStatusAndBookTitleContaining(LoanStatus status, String title);
    
    @Query("SELECT l FROM Loan l WHERE l.user.email LIKE %?1% AND l.book.title LIKE %?2%")
    List<Loan> findByUserEmailContainingAndBookTitleContaining(String email, String title);
    
    @Query("SELECT l FROM Loan l WHERE l.status = ?1 AND l.user.email LIKE %?2% AND l.book.title LIKE %?3%")
    List<Loan> findByStatusAndUserEmailContainingAndBookTitleContaining(LoanStatus status, String email, String title);

    Collection<Object> findByUserEmail(String email);

    Collection<Object> findByUserEmailAndStatus(String email, LoanStatus loanStatus);

    boolean existsByBookIsbnAndStatus(String isbn, LoanStatus loanStatus);

    boolean existsByUserEmailAndStatus(String email, LoanStatus loanStatus);

    int countByUserEmailAndStatus(String email, LoanStatus loanStatus);
}
