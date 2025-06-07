package com.biblioteca.biblioteca.repository;

import com.biblioteca.biblioteca.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    Book findByIsbn(String isbn);
    
    List<Book> findByTitleContainingIgnoreCase(String title);
    
    List<Book> findByAuthorContainingIgnoreCase(String author);
    
    List<Book> findByGenreContainingIgnoreCase(String genre);
    
    List<Book> findByTitleContainingIgnoreCaseAndAuthorContainingIgnoreCase(String title, String author);
    
    List<Book> findByTitleContainingIgnoreCaseAndGenreContainingIgnoreCase(String title, String genre);
    
    List<Book> findByAuthorContainingIgnoreCaseAndGenreContainingIgnoreCase(String author, String genre);
    
    List<Book> findByTitleContainingIgnoreCaseAndAuthorContainingIgnoreCaseAndGenreContainingIgnoreCase(String title, String author, String genre);
}
