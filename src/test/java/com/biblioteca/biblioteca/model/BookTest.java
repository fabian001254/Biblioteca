package com.biblioteca.biblioteca.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BookTest {

    private Book book;
    private final Long TEST_ID = 1L;
    private final String TEST_ISBN = "978-3-16-148410-0";
    private final String TEST_TITLE = "Test Book";
    private final String TEST_AUTHOR = "Test Author";
    private final String TEST_GENRE = "Fiction";
    private final int TEST_YEAR = 2023;
    private final long TEST_QUANTITY = 5L;

    @BeforeEach
    void setUp() {
        book = new Book();
        book.setId(TEST_ID);
        book.setIsbn(TEST_ISBN);
        book.setTitle(TEST_TITLE);
        book.setAuthor(TEST_AUTHOR);
        book.setGenre(TEST_GENRE);
        book.setPublicationYear(TEST_YEAR);
        book.setQuantity(TEST_QUANTITY);
    }

    @Test
    void testBookCreation() {
        assertNotNull(book);
        assertEquals(TEST_ID, book.getId());
        assertEquals(TEST_ISBN, book.getIsbn());
        assertEquals(TEST_TITLE, book.getTitle());
        assertEquals(TEST_AUTHOR, book.getAuthor());
        assertEquals(TEST_GENRE, book.getGenre());
        assertEquals(TEST_YEAR, book.getPublicationYear());
        assertEquals(TEST_QUANTITY, book.getQuantity());
    }

    @Test
    void testBookSetters() {
        String newTitle = "Updated Title";
        String newAuthor = "New Author";
        String newGenre = "Non-Fiction";
        int newYear = 2024;
        long newQuantity = 10L;
        
        book.setTitle(newTitle);
        book.setAuthor(newAuthor);
        book.setGenre(newGenre);
        book.setPublicationYear(newYear);
        book.setQuantity(newQuantity);
        
        assertEquals(newTitle, book.getTitle());
        assertEquals(newAuthor, book.getAuthor());
        assertEquals(newGenre, book.getGenre());
        assertEquals(newYear, book.getPublicationYear());
        assertEquals(newQuantity, book.getQuantity());
    }

    @Test
    void testBookAllArgsConstructor() {
        Book fullBook = new Book(TEST_ID, TEST_ISBN, TEST_TITLE, TEST_AUTHOR, TEST_GENRE, TEST_YEAR, TEST_QUANTITY);
        
        assertNotNull(fullBook);
        assertEquals(TEST_ID, fullBook.getId());
        assertEquals(TEST_ISBN, fullBook.getIsbn());
        assertEquals(TEST_TITLE, fullBook.getTitle());
        assertEquals(TEST_AUTHOR, fullBook.getAuthor());
        assertEquals(TEST_GENRE, fullBook.getGenre());
        assertEquals(TEST_YEAR, fullBook.getPublicationYear());
        assertEquals(TEST_QUANTITY, fullBook.getQuantity());
    }
}
