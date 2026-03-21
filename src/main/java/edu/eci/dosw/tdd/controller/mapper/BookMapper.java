package edu.eci.dosw.tdd.controller.mapper;

import edu.eci.dosw.tdd.controller.dto.BookDTO;
import edu.eci.dosw.tdd.core.model.Book;

public class BookMapper {
    public static BookDTO toDTO(Book book) {
        if (book == null) return null;

        return new BookDTO(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getTotalCopies(),
                book.getAvailableCopies()
        );
    }

    public static Book toEntity(BookDTO bookDTO) {
        if (bookDTO == null) return null;

        return new Book(
                bookDTO.getId(),
                bookDTO.getTitle(),
                bookDTO.getAuthor(),
                bookDTO.getTotalCopies(),
                bookDTO.getAvailableCopies()
        );
    }
}
