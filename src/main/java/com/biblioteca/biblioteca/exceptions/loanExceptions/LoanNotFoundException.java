package com.biblioteca.biblioteca.exceptions.loanExceptions;

public class LoanNotFoundException extends RuntimeException {
    public LoanNotFoundException() {
        super("Loan not found");
    }
}
