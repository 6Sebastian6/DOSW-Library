package edu.eci.dosw.tdd.core.validator;

import edu.eci.dosw.tdd.core.model.Book;

public class BookValidator {

    public static void validate(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("El libro no puede ser nulo");
        }
        if (book.getTitle() == null || book.getTitle().isBlank()) {
            throw new IllegalArgumentException("El título del libro es obligatorio");
        }
        if (book.getAuthor() == null || book.getAuthor().isBlank()) {
            throw new IllegalArgumentException("El autor del libro es obligatorio");
        }
        if (book.getTotalCopies() <= 0) {
            throw new IllegalArgumentException("El número de copias debe ser mayor a cero");
        }
    }
}

