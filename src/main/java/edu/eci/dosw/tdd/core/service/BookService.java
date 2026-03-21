package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.util.IdGeneratorUtil;
import edu.eci.dosw.tdd.exception.BookNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BookService {

    private final Map<String, Book> books = new HashMap<>();

    public List<Book> getAllBooks() {
        return books.values().stream().toList();
    }

    public Book getBookById(String id) {
        Book book = books.get(id);
        if (book == null) {
            throw new BookNotFoundException("Libro no encontrado con ID: " + id);
        }
        return book;
    }

    public Book createBook(Book book) {
        book.setId(IdGeneratorUtil.generateBookId());
        book.setAvailableCopies(book.getTotalCopies());
        books.put(book.getId(), book);
        return book;
    }

    public Book updateBook(String id, Book bookDetails) {
        Book book = getBookById(id);
        book.setTitle(bookDetails.getTitle());
        book.setAuthor(bookDetails.getAuthor());

        if (bookDetails.getTotalCopies() != book.getTotalCopies()) {
            int difference = bookDetails.getTotalCopies() - book.getTotalCopies();
            book.setTotalCopies(bookDetails.getTotalCopies());
            book.setAvailableCopies(book.getAvailableCopies() + difference);
        }

        return book;
    }

    public void deleteBook(String id) {
        if (!books.containsKey(id)) {
            throw new BookNotFoundException("Libro no encontrado con ID: " + id);
        }
        books.remove(id);
    }

    public List<Book> searchBooksByTitle(String title) {
        return books.values().stream()
                .filter(book -> book.getTitle().toLowerCase().contains(title.toLowerCase()))
                .toList();
    }

    public boolean isBookAvailable(String id) {
        Book book = getBookById(id);
        return book.getAvailableCopies() > 0;
    }
}
