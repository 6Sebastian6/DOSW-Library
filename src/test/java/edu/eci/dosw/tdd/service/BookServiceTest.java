package edu.eci.dosw.tdd.service;

import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.service.BookService;
import edu.eci.dosw.tdd.exception.BookNotFoundException;
import edu.eci.dosw.tdd.persistence.entity.BookEntity;
import edu.eci.dosw.tdd.persistence.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BookService - Pruebas unitarias")
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    private BookEntity sampleEntity;

    @BeforeEach
    void setUp() {
        sampleEntity = new BookEntity("BOK-001", "Clean Code", "Robert C. Martin", 3, 3);
    }

    // ---- createBook ----

    @Test
    @DisplayName("Crear libro válido genera ID y lo guarda en repositorio")
    void createBook_valid_savesAndReturns() {
        when(bookRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Book book = new Book(null, "Clean Code", "Robert C. Martin", 3, 0);
        Book result = bookService.createBook(book);

        assertNotNull(result.getId());
        assertTrue(result.getId().startsWith("BOK-"));
        assertEquals(3, result.getAvailableCopies());
        verify(bookRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Crear libro con título vacío lanza excepción sin guardar")
    void createBook_emptyTitle_throwsException() {
        Book book = new Book(null, "", "Autor", 1, 0);
        assertThrows(IllegalArgumentException.class, () -> bookService.createBook(book));
        verify(bookRepository, never()).save(any());
    }

    @Test
    @DisplayName("Crear libro con autor vacío lanza excepción")
    void createBook_emptyAuthor_throwsException() {
        Book book = new Book(null, "Título", "", 1, 0);
        assertThrows(IllegalArgumentException.class, () -> bookService.createBook(book));
    }

    @Test
    @DisplayName("Crear libro con copias en cero lanza excepción")
    void createBook_zeroCopies_throwsException() {
        Book book = new Book(null, "Título", "Autor", 0, 0);
        assertThrows(IllegalArgumentException.class, () -> bookService.createBook(book));
    }

    // ---- getAllBooks ----

    @Test
    @DisplayName("getAllBooks retorna todos los libros del repositorio")
    void getAllBooks_returnsAll() {
        when(bookRepository.findAll()).thenReturn(List.of(sampleEntity));
        List<Book> books = bookService.getAllBooks();
        assertEquals(1, books.size());
        assertEquals("Clean Code", books.get(0).getTitle());
    }

    @Test
    @DisplayName("getAllBooks retorna lista vacía cuando no hay libros")
    void getAllBooks_emptyRepository_returnsEmpty() {
        when(bookRepository.findAll()).thenReturn(List.of());
        assertTrue(bookService.getAllBooks().isEmpty());
    }

    // ---- getBookById ----

    @Test
    @DisplayName("getBookById retorna libro cuando existe")
    void getBookById_existing_returnsBook() {
        when(bookRepository.findById("BOK-001")).thenReturn(Optional.of(sampleEntity));
        Book book = bookService.getBookById("BOK-001");
        assertEquals("BOK-001", book.getId());
        assertEquals("Clean Code", book.getTitle());
    }

    @Test
    @DisplayName("getBookById lanza BookNotFoundException para ID inexistente")
    void getBookById_nonExistent_throwsException() {
        when(bookRepository.findById("INVALID")).thenReturn(Optional.empty());
        assertThrows(BookNotFoundException.class, () -> bookService.getBookById("INVALID"));
    }

    // ---- updateBook ----

    @Test
    @DisplayName("updateBook modifica título y autor en el repositorio")
    void updateBook_valid_updatesFields() {
        when(bookRepository.findById("BOK-001")).thenReturn(Optional.of(sampleEntity));
        when(bookRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Book update = new Book(null, "Nuevo Título", "Nuevo Autor", 3, 0);
        Book result = bookService.updateBook("BOK-001", update);

        assertEquals("Nuevo Título", result.getTitle());
        assertEquals("Nuevo Autor", result.getAuthor());
        verify(bookRepository).save(any());
    }

    @Test
    @DisplayName("updateBook ajusta copias disponibles al aumentar el total")
    void updateBook_increaseTotalCopies_adjustsAvailable() {
        when(bookRepository.findById("BOK-001")).thenReturn(Optional.of(sampleEntity));
        when(bookRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Book update = new Book(null, "Clean Code", "Martin", 5, 0);
        Book result = bookService.updateBook("BOK-001", update);

        assertEquals(5, result.getAvailableCopies());
    }

    @Test
    @DisplayName("updateBook con ID inexistente lanza BookNotFoundException")
    void updateBook_nonExistent_throwsException() {
        when(bookRepository.findById("INVALID")).thenReturn(Optional.empty());
        Book update = new Book(null, "Título", "Autor", 1, 0);
        assertThrows(BookNotFoundException.class, () -> bookService.updateBook("INVALID", update));
    }

    // ---- deleteBook ----

    @Test
    @DisplayName("deleteBook llama a deleteById cuando el libro existe")
    void deleteBook_existing_deletesFromRepository() {
        when(bookRepository.existsById("BOK-001")).thenReturn(true);
        bookService.deleteBook("BOK-001");
        verify(bookRepository).deleteById("BOK-001");
    }

    @Test
    @DisplayName("deleteBook con ID inexistente lanza BookNotFoundException")
    void deleteBook_nonExistent_throwsException() {
        when(bookRepository.existsById("INVALID")).thenReturn(false);
        assertThrows(BookNotFoundException.class, () -> bookService.deleteBook("INVALID"));
        verify(bookRepository, never()).deleteById(any());
    }

    // ---- searchBooksByTitle ----

    @Test
    @DisplayName("searchBooksByTitle retorna libros que coinciden")
    void searchBooksByTitle_match_returnsResults() {
        when(bookRepository.findByTitleContainingIgnoreCase("clean"))
                .thenReturn(List.of(sampleEntity));
        List<Book> results = bookService.searchBooksByTitle("clean");
        assertEquals(1, results.size());
    }

    @Test
    @DisplayName("searchBooksByTitle sin coincidencias retorna lista vacía")
    void searchBooksByTitle_noMatch_returnsEmpty() {
        when(bookRepository.findByTitleContainingIgnoreCase("xyz")).thenReturn(List.of());
        assertTrue(bookService.searchBooksByTitle("xyz").isEmpty());
    }

    // ---- isBookAvailable ----

    @Test
    @DisplayName("isBookAvailable retorna true cuando hay copias disponibles")
    void isBookAvailable_withCopies_returnsTrue() {
        when(bookRepository.findById("BOK-001")).thenReturn(Optional.of(sampleEntity));
        assertTrue(bookService.isBookAvailable("BOK-001"));
    }

    @Test
    @DisplayName("isBookAvailable retorna false cuando no hay copias")
    void isBookAvailable_noCopies_returnsFalse() {
        BookEntity noCopies = new BookEntity("BOK-002", "Agotado", "Autor", 1, 0);
        when(bookRepository.findById("BOK-002")).thenReturn(Optional.of(noCopies));
        assertFalse(bookService.isBookAvailable("BOK-002"));
    }
}