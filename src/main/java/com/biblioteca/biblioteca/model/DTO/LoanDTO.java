package com.biblioteca.biblioteca.model.DTO;

import com.biblioteca.biblioteca.model.Enums.LoanStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoanDTO {
    private Long id;
    private Long bookId;
    private String bookTitle;
    private Long userId;
    private String userName;
    private LocalDateTime loanDate;
    private LocalDate dueDate;
    private LocalDateTime returnDate;
    private LoanStatus status;
    private String notes;
}