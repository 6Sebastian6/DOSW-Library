package edu.eci.dosw.tdd.controller;

import edu.eci.dosw.tdd.controller.dto.BookDTO;
import edu.eci.dosw.tdd.controller.mapper.BookMapper;
import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@Tag(name = "Libros", description = "Operaciones sobre el catálogo de libros")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    @Operation(summary = "Obtener todos los libros")
    @ApiResponse(responseCode = "200", description = "Lista de libros obtenida exitosamente")
    public ResponseEntity<List<BookDTO>> getAllBooks() {
        List<BookDTO> books = bookService.getAllBooks().stream()
                .map(BookMapper::toDTO)
                .toList();
        return ResponseEntity.ok(books);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un libro por ID")
    @ApiResponse(responseCode = "200", description = "Libro encontrado")
    @ApiResponse(responseCode = "404", description = "Libro no encontrado")
    public ResponseEntity<BookDTO> getBookById(@PathVariable String id) {
        Book book = bookService.getBookById(id);
        return ResponseEntity.ok(BookMapper.toDTO(book));
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo libro")
    @ApiResponse(responseCode = "201", description = "Libro creado exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos inválidos")
    public ResponseEntity<BookDTO> createBook(@Valid @RequestBody BookDTO bookDTO) {
        Book book = BookMapper.toEntity(bookDTO);
        Book createdBook = bookService.createBook(book);
        return new ResponseEntity<>(BookMapper.toDTO(createdBook), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un libro existente")
    @ApiResponse(responseCode = "200", description = "Libro actualizado exitosamente")
    @ApiResponse(responseCode = "404", description = "Libro no encontrado")
    public ResponseEntity<BookDTO> updateBook(@PathVariable String id, @Valid @RequestBody BookDTO bookDTO) {
        Book book = BookMapper.toEntity(bookDTO);
        Book updatedBook = bookService.updateBook(id, book);
        return ResponseEntity.ok(BookMapper.toDTO(updatedBook));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un libro")
    @ApiResponse(responseCode = "204", description = "Libro eliminado exitosamente")
    @ApiResponse(responseCode = "404", description = "Libro no encontrado")
    public ResponseEntity<Void> deleteBook(@PathVariable String id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar libros por título")
    @ApiResponse(responseCode = "200", description = "Resultados de búsqueda")
    public ResponseEntity<List<BookDTO>> searchBooksByTitle(@RequestParam String title) {
        List<BookDTO> books = bookService.searchBooksByTitle(title).stream()
                .map(BookMapper::toDTO)
                .toList();
        return ResponseEntity.ok(books);
    }
}
