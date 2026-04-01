package edu.eci.dosw.tdd.service;

import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.service.BookService;
import edu.eci.dosw.tdd.exception.BookNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BookService - Pruebas unitarias")
class BookServiceTest {

    private BookService bookService;

    @BeforeEach
    void setUp() {
        bookService = new BookService();
    }

    // ---- createBook ----

    @Test
    @DisplayName("Crear libro válido genera ID y copias disponibles")
    void createBook_validBook_setsIdAndAvailableCopies() {
        Book book = new Book(null, "Clean Code", "Robert C. Martin", 3, 0);
        Book result = bookService.createBook(book);

        assertNotNull(result.getId());
        assertTrue(result.getId().startsWith("BOK-"));
        assertEquals(3, result.getAvailableCopies());
        assertEquals("Clean Code", result.getTitle());
    }

    @Test
    @DisplayName("Crear libro con título vacío lanza excepción")
    void createBook_emptyTitle_throwsException() {
        Book book = new Book(null, "", "Autor", 1, 0);
        assertThrows(IllegalArgumentException.class, () -> bookService.createBook(book));
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
    @DisplayName("getAllBooks retorna lista vacía al inicio")
    void getAllBooks_emptyInitially() {
        assertTrue(bookService.getAllBooks().isEmpty());
    }

    @Test
    @DisplayName("getAllBooks retorna todos los libros creados")
    void getAllBooks_returnsAllBooks() {
        bookService.createBook(new Book(null, "Libro A", "Autor A", 2, 0));
        bookService.createBook(new Book(null, "Libro B", "Autor B", 1, 0));
        assertEquals(2, bookService.getAllBooks().size());
    }

    // ---- getBookById ----

    @Test
    @DisplayName("getBookById retorna libro existente")
    void getBookById_existingId_returnsBook() {
        Book created = bookService.createBook(new Book(null, "Java", "Autor", 1, 0));
        Book found = bookService.getBookById(created.getId());
        assertEquals(created.getId(), found.getId());
    }

    @Test
    @DisplayName("getBookById lanza excepción para ID inexistente")
    void getBookById_nonExistentId_throwsBookNotFoundException() {
        assertThrows(BookNotFoundException.class, () -> bookService.getBookById("INVALID"));
    }

    // ---- updateBook ----

    @Test
    @DisplayName("updateBook modifica título y autor correctamente")
    void updateBook_validData_updatesFields() {
        Book created = bookService.createBook(new Book(null, "Original", "Autor", 2, 0));
        Book update = new Book(null, "Nuevo Título", "Nuevo Autor", 2, 0);
        Book updated = bookService.updateBook(created.getId(), update);

        assertEquals("Nuevo Título", updated.getTitle());
        assertEquals("Nuevo Autor", updated.getAuthor());
    }

    @Test
    @DisplayName("updateBook ajusta copias disponibles al cambiar total")
    void updateBook_increaseTotalCopies_adjustsAvailableCopies() {
        Book created = bookService.createBook(new Book(null, "Libro", "Autor", 2, 0));
        Book update = new Book(null, "Libro", "Autor", 4, 0);
        Book updated = bookService.updateBook(created.getId(), update);

        assertEquals(4, updated.getAvailableCopies());
    }

    @Test
    @DisplayName("updateBook con ID inexistente lanza excepción")
    void updateBook_nonExistentId_throwsException() {
        Book update = new Book(null, "Título", "Autor", 1, 0);
        assertThrows(BookNotFoundException.class, () -> bookService.updateBook("INVALID", update));
    }

    // ---- deleteBook ----

    @Test
    @DisplayName("deleteBook elimina libro existente")
    void deleteBook_existingId_removesBook() {
        Book created = bookService.createBook(new Book(null, "Libro", "Autor", 1, 0));
        bookService.deleteBook(created.getId());
        assertThrows(BookNotFoundException.class, () -> bookService.getBookById(created.getId()));
    }

    @Test
    @DisplayName("deleteBook con ID inexistente lanza excepción")
    void deleteBook_nonExistentId_throwsException() {
        assertThrows(BookNotFoundException.class, () -> bookService.deleteBook("INVALID"));
    }

    // ---- searchBooksByTitle ----

    @Test
    @DisplayName("searchBooksByTitle encuentra libros que contienen el término")
    void searchBooksByTitle_matchingTitle_returnsResults() {
        bookService.createBook(new Book(null, "Clean Code", "Martin", 1, 0));
        bookService.createBook(new Book(null, "The Clean Coder", "Martin", 1, 0));
        bookService.createBook(new Book(null, "Refactoring", "Fowler", 1, 0));

        List<Book> results = bookService.searchBooksByTitle("clean");
        assertEquals(2, results.size());
    }

    @Test
    @DisplayName("searchBooksByTitle es insensible a mayúsculas")
    void searchBooksByTitle_caseInsensitive() {
        bookService.createBook(new Book(null, "Clean Code", "Martin", 1, 0));
        List<Book> results = bookService.searchBooksByTitle("CLEAN");
        assertEquals(1, results.size());
    }

    @Test
    @DisplayName("searchBooksByTitle sin coincidencias retorna lista vacía")
    void searchBooksByTitle_noMatch_returnsEmpty() {
        bookService.createBook(new Book(null, "Clean Code", "Martin", 1, 0));
        List<Book> results = bookService.searchBooksByTitle("xyz");
        assertTrue(results.isEmpty());
    }

    // ---- isBookAvailable ----

    @Test
    @DisplayName("isBookAvailable retorna true cuando hay copias")
    void isBookAvailable_withCopies_returnsTrue() {
        Book created = bookService.createBook(new Book(null, "Libro", "Autor", 2, 0));
        assertTrue(bookService.isBookAvailable(created.getId()));
    }
}