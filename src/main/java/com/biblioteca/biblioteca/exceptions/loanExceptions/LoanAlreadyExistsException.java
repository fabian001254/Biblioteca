package com.biblioteca.biblioteca.exceptions.loanExceptions;

public class LoanAlreadyExistsException extends RuntimeException {
    public LoanAlreadyExistsException() {
        super("Loan already exists");
    }
}
