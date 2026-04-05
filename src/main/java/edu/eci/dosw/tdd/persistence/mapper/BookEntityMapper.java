package edu.eci.dosw.tdd.persistence.mapper;

import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.persistence.entity.BookEntity;

public class BookEntityMapper {

    private BookEntityMapper() {}

    public static Book toDomain(BookEntity entity) {
        if (entity == null) return null;
        return new Book(
                entity.getId(),
                entity.getTitle(),
                entity.getAuthor(),
                entity.getTotalCopies(),
                entity.getAvailableCopies()
        );
    }

    public static BookEntity toEntity(Book book) {
        if (book == null) return null;
        return BookEntity.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .totalCopies(book.getTotalCopies())
                .availableCopies(book.getAvailableCopies())
                .build();
    }
}
