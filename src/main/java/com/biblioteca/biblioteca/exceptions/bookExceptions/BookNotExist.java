package com.biblioteca.biblioteca.exceptions.bookExceptions;

public class BookNotExist extends RuntimeException {
    public BookNotExist() {
        super("Book not exist");
    }
}
