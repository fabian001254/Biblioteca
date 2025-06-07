package com.biblioteca.biblioteca.service;

import com.biblioteca.biblioteca.exceptions.bookExceptions.BookAlreadyExistException;
import com.biblioteca.biblioteca.exceptions.bookExceptions.BookNotExist;
import com.biblioteca.biblioteca.mapper.BookMapper;
import com.biblioteca.biblioteca.model.Book;
import com.biblioteca.biblioteca.model.DTO.BookDTO;
import com.biblioteca.biblioteca.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.stream.Collectors;

import java.util.List;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookMapper bookMapper;

    public boolean addBook(BookDTO bookDTO) throws BookAlreadyExistException {
        Book existingBook = bookRepository.findByIsbn(bookDTO.getIsbn());
        if (existingBook != null) {
            throw new BookAlreadyExistException();
        }

        Book newBook = bookMapper.BookDTOtoBook(bookDTO);
        bookRepository.save(newBook);
        return true;
    }

    public BookDTO getBook(String isbn) {
        Book book = bookRepository.findByIsbn(isbn);
        if (book != null) {
            return bookMapper.BooktoBookDTO(book);
        }
        return null;
    }

    public boolean updateBook(BookDTO bookDTO) {
        Book existingBook = bookRepository.findByIsbn(bookDTO.getIsbn());
        if (existingBook != null) {
            Book updatedBook = bookMapper.BookDTOtoBook(bookDTO);
            bookRepository.save(updatedBook);
            return true;
        }
        return false;
    }

    public boolean deleteBook(String isbn) {
        Book existingBook = bookRepository.findByIsbn(isbn);
        if (existingBook != null) {
            bookRepository.delete(existingBook);
            return true;
        }
        return false;
    }

    public List<BookDTO> getAllBooks() {
        List<Book> books = bookRepository.findAll();
        return books.stream()
                .map(bookMapper::BooktoBookDTO)
                .collect(Collectors.toList());
    }

    public BookDTO bookExist(String isbn) {
        Book book = bookRepository.findByIsbn(isbn);
        if(book != null){
            return bookMapper.BooktoBookDTO(book);
        }
        throw new BookNotExist();
    }


}
