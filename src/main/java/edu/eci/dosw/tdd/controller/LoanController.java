package edu.eci.dosw.tdd.controller;

import edu.eci.dosw.tdd.controller.dto.LoanDTO;
import edu.eci.dosw.tdd.controller.mapper.LoanMapper;
import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.core.service.LoanService;
import edu.eci.dosw.tdd.core.validator.LoanValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
@Tag(name = "Préstamos", description = "Operaciones sobre préstamos de libros")
@SecurityRequirement(name = "bearerAuth")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    // Solo LIBRARIAN puede ver todos los préstamos
    @GetMapping
    @PreAuthorize("hasRole('LIBRARIAN')")
    @Operation(summary = "Obtener todos los préstamos (solo LIBRARIAN)")
    @ApiResponse(responseCode = "200", description = "Lista de préstamos")
    @ApiResponse(responseCode = "403", description = "Sin permisos")
    public ResponseEntity<List<LoanDTO>> getAllLoans() {
        List<LoanDTO> loans = loanService.getAllLoans().stream()
                .map(LoanMapper::toDTO).toList();
        return ResponseEntity.ok(loans);
    }

    // Solo LIBRARIAN puede consultar préstamo por ID
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('LIBRARIAN')")
    @Operation(summary = "Obtener un préstamo por ID (solo LIBRARIAN)")
    @ApiResponse(responseCode = "200", description = "Préstamo encontrado")
    @ApiResponse(responseCode = "404", description = "Préstamo no encontrado")
    public ResponseEntity<LoanDTO> getLoanById(@PathVariable String id) {
        return ResponseEntity.ok(LoanMapper.toDTO(loanService.getLoanById(id)));
    }

    // USER puede solicitar préstamos
    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'LIBRARIAN')")
    @Operation(summary = "Crear un nuevo préstamo")
    @ApiResponse(responseCode = "201", description = "Préstamo creado")
    @ApiResponse(responseCode = "404", description = "Usuario o libro no encontrado")
    @ApiResponse(responseCode = "409", description = "Límite alcanzado o libro no disponible")
    public ResponseEntity<LoanDTO> createLoan(@RequestParam String userId,
                                              @RequestParam String bookId) {
        LoanValidator.validateUserId(userId);
        LoanValidator.validateBookId(bookId);
        Loan loan = loanService.createLoan(userId, bookId);
        return new ResponseEntity<>(LoanMapper.toDTO(loan), HttpStatus.CREATED);
    }

    // USER puede devolver libros
    @PutMapping("/{id}/return")
    @PreAuthorize("hasAnyRole('USER', 'LIBRARIAN')")
    @Operation(summary = "Devolver un libro prestado")
    @ApiResponse(responseCode = "200", description = "Libro devuelto")
    @ApiResponse(responseCode = "400", description = "El libro ya fue devuelto")
    @ApiResponse(responseCode = "404", description = "Préstamo no encontrado")
    public ResponseEntity<LoanDTO> returnBook(@PathVariable String id) {
        return ResponseEntity.ok(LoanMapper.toDTO(loanService.returnBook(id)));
    }

    // USER solo puede ver SUS PROPIOS préstamos activos
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('LIBRARIAN') or #currentUser.username == @userService.getUserById(#userId).username")
    @Operation(summary = "Obtener préstamos activos de un usuario")
    @ApiResponse(responseCode = "200", description = "Lista de préstamos activos")
    @ApiResponse(responseCode = "403", description = "No puede ver préstamos de otro usuario")
    public ResponseEntity<List<LoanDTO>> getActiveLoansByUser(
            @PathVariable String userId,
            @AuthenticationPrincipal UserDetails currentUser) {
        List<LoanDTO> loans = loanService.getActiveLoansByUser(userId).stream()
                .map(LoanMapper::toDTO).toList();
        return ResponseEntity.ok(loans);
    }
}