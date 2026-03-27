package edu.eci.dosw.tdd.controller;

import edu.eci.dosw.tdd.controller.dto.LoanDTO;
import edu.eci.dosw.tdd.controller.mapper.LoanMapper;
import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.core.service.LoanService;
import edu.eci.dosw.tdd.core.validator.LoanValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
@Tag(name = "Préstamos", description = "Operaciones sobre préstamos de libros")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @GetMapping
    @Operation(summary = "Obtener todos los préstamos")
    @ApiResponse(responseCode = "200", description = "Lista de préstamos obtenida exitosamente")
    public ResponseEntity<List<LoanDTO>> getAllLoans() {
        List<LoanDTO> loans = loanService.getAllLoans().stream()
                .map(LoanMapper::toDTO)
                .toList();
        return ResponseEntity.ok(loans);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un préstamo por ID")
    @ApiResponse(responseCode = "200", description = "Préstamo encontrado")
    @ApiResponse(responseCode = "404", description = "Préstamo no encontrado")
    public ResponseEntity<LoanDTO> getLoanById(@PathVariable String id) {
        Loan loan = loanService.getLoanById(id);
        return ResponseEntity.ok(LoanMapper.toDTO(loan));
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo préstamo")
    @ApiResponse(responseCode = "201", description = "Préstamo creado exitosamente")
    @ApiResponse(responseCode = "404", description = "Usuario o libro no encontrado")
    @ApiResponse(responseCode = "409", description = "Límite de préstamos alcanzado o libro no disponible")
    public ResponseEntity<LoanDTO> createLoan(@RequestParam String userId, @RequestParam String bookId) {
        LoanValidator.validateUserId(userId);
        LoanValidator.validateBookId(bookId);
        Loan loan = loanService.createLoan(userId, bookId);
        return new ResponseEntity<>(LoanMapper.toDTO(loan), HttpStatus.CREATED);
    }

    @PutMapping("/{id}/return")
    @Operation(summary = "Devolver un libro prestado")
    @ApiResponse(responseCode = "200", description = "Libro devuelto exitosamente")
    @ApiResponse(responseCode = "404", description = "Préstamo no encontrado")
    @ApiResponse(responseCode = "400", description = "El libro ya fue devuelto")
    public ResponseEntity<LoanDTO> returnBook(@PathVariable String id) {
        Loan loan = loanService.returnBook(id);
        return ResponseEntity.ok(LoanMapper.toDTO(loan));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Obtener préstamos activos de un usuario")
    @ApiResponse(responseCode = "200", description = "Lista de préstamos activos del usuario")
    public ResponseEntity<List<LoanDTO>> getActiveLoansByUser(@PathVariable String userId) {
        List<LoanDTO> loans = loanService.getActiveLoansByUser(userId).stream()
                .map(LoanMapper::toDTO)
                .toList();
        return ResponseEntity.ok(loans);
    }
}
