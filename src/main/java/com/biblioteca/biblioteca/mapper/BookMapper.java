package com.biblioteca.biblioteca.mapper;

import com.biblioteca.biblioteca.model.Book;
import com.biblioteca.biblioteca.model.DTO.BookDTO;
import org.springframework.stereotype.Component;

@Component
public class BookMapper {
    public BookDTO BooktoBookDTO(Book book) {
        BookDTO bookDTO = new BookDTO();
        book.setAuthor(book.getAuthor());
        book.setIsbn(book.getIsbn());
        book.setTitle(book.getTitle());
        book.setGenre(book.getGenre());
        book.setPublicationYear(book.getPublicationYear());
        book.setQuantity(book.getQuantity());
        return bookDTO;
    }

    public Book BookDTOtoBook(BookDTO bookDTO) {
        Book book = new Book();
        book.setAuthor(bookDTO.getAuthor());
        book.setIsbn(bookDTO.getIsbn());
        book.setTitle(bookDTO.getTitle());
        book.setGenre(bookDTO.getGenre());
        book.setPublicationYear(bookDTO.getYear());
        book.setQuantity(bookDTO.getQuantity());
        return book;
    }
}
