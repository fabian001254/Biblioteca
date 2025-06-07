package com.biblioteca.biblioteca.model.DTO;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BookDTO {
    private Long id;
    private String isbn;
    private String title;
    private String author;
    private String genre;
    private int year;
    private Long quantity;
}
