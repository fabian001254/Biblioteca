package com.biblioteca.biblioteca.exceptions.bookExceptions;

public class BookNotAvailableException extends RuntimeException {
    public BookNotAvailableException(){
        super("Book not available");
    }
}
