package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.util.IdGeneratorUtil;
import edu.eci.dosw.tdd.core.validator.BookValidator;
import edu.eci.dosw.tdd.exception.BookNotFoundException;
import edu.eci.dosw.tdd.persistence.entity.BookEntity;
import edu.eci.dosw.tdd.persistence.mapper.BookEntityMapper;
import edu.eci.dosw.tdd.persistence.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(BookEntityMapper::toDomain)
                .toList();
    }

    public Book getBookById(String id) {
        return bookRepository.findById(id)
                .map(BookEntityMapper::toDomain)
                .orElseThrow(() -> new BookNotFoundException("Libro no encontrado con ID: " + id));
    }

    public Book createBook(Book book) {
        BookValidator.validate(book);
        book.setId(IdGeneratorUtil.generateBookId());
        book.setAvailableCopies(book.getTotalCopies());
        BookEntity saved = bookRepository.save(BookEntityMapper.toEntity(book));
        return BookEntityMapper.toDomain(saved);
    }

    public Book updateBook(String id, Book bookDetails) {
        BookValidator.validate(bookDetails);
        BookEntity existing = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Libro no encontrado con ID: " + id));

        existing.setTitle(bookDetails.getTitle());
        existing.setAuthor(bookDetails.getAuthor());

        if (bookDetails.getTotalCopies() != existing.getTotalCopies()) {
            int difference = bookDetails.getTotalCopies() - existing.getTotalCopies();
            existing.setTotalCopies(bookDetails.getTotalCopies());
            existing.setAvailableCopies(existing.getAvailableCopies() + difference);
        }

        return BookEntityMapper.toDomain(bookRepository.save(existing));
    }

    public void deleteBook(String id) {
        if (!bookRepository.existsById(id)) {
            throw new BookNotFoundException("Libro no encontrado con ID: " + id);
        }
        bookRepository.deleteById(id);
    }

    public List<Book> searchBooksByTitle(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title).stream()
                .map(BookEntityMapper::toDomain)
                .toList();
    }

    public boolean isBookAvailable(String id) {
        return bookRepository.findById(id)
                .map(b -> b.getAvailableCopies() > 0)
                .orElseThrow(() -> new BookNotFoundException("Libro no encontrado con ID: " + id));
    }
}