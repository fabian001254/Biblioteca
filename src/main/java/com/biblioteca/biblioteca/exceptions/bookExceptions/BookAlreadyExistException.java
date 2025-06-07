package com.biblioteca.biblioteca.exceptions.bookExceptions;

public class BookAlreadyExistException extends Exception {
    public BookAlreadyExistException() {
        super("Book already exists");
    }
}
