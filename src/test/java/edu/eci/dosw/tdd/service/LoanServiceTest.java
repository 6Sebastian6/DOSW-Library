package edu.eci.dosw.tdd.service;

import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.core.model.Role;
import edu.eci.dosw.tdd.core.model.Status;
import edu.eci.dosw.tdd.core.service.LoanService;
import edu.eci.dosw.tdd.exception.BookNotAvailableException;
import edu.eci.dosw.tdd.exception.BookNotFoundException;
import edu.eci.dosw.tdd.exception.LoanLimitExceededException;
import edu.eci.dosw.tdd.exception.LoanNotFoundException;
import edu.eci.dosw.tdd.exception.UserNotFoundException;
import edu.eci.dosw.tdd.persistence.entity.BookEntity;
import edu.eci.dosw.tdd.persistence.entity.LoanEntity;
import edu.eci.dosw.tdd.persistence.entity.UserEntity;
import edu.eci.dosw.tdd.persistence.repository.BookRepository;
import edu.eci.dosw.tdd.persistence.repository.LoanRepository;
import edu.eci.dosw.tdd.persistence.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LoanService - Pruebas unitarias")
class LoanServiceTest {

    @Mock private LoanRepository loanRepository;
    @Mock private BookRepository bookRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks
    private LoanService loanService;

    private UserEntity userEntity;
    private BookEntity bookEntity;
    private LoanEntity activeLoanEntity;

    @BeforeEach
    void setUp() {
        userEntity = new UserEntity("USR-001", "Juan Pérez", "juanp", "hashed", Role.USER);
        bookEntity = new BookEntity("BOK-001", "Clean Code", "Martin", 3, 2);
        activeLoanEntity = LoanEntity.builder()
                .id("LOAN-001")
                .user(userEntity)
                .book(bookEntity)
                .loanDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(7))
                .status(Status.ACTIVE)
                .build();
    }

    // ---- createLoan ----

    @Test
    @DisplayName("Crear préstamo válido reduce copias disponibles y persiste")
    void createLoan_valid_createsAndReducesCopies() {
        when(loanRepository.countByUserIdAndStatus("USR-001", Status.ACTIVE)).thenReturn(0L);
        when(userRepository.findById("USR-001")).thenReturn(Optional.of(userEntity));
        when(bookRepository.findById("BOK-001")).thenReturn(Optional.of(bookEntity));
        when(bookRepository.save(any())).thenReturn(bookEntity);
        when(loanRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Loan loan = loanService.createLoan("USR-001", "BOK-001");

        assertNotNull(loan.getId());
        assertEquals(Status.ACTIVE, loan.getStatus());
        assertEquals(1, bookEntity.getAvailableCopies()); // de 2 bajó a 1
        verify(loanRepository).save(any());
        verify(bookRepository).save(bookEntity);
    }

    @Test
    @DisplayName("Crear préstamo lanza excepción cuando el usuario tiene 3 activos")
    void createLoan_limitExceeded_throwsException() {
        when(loanRepository.countByUserIdAndStatus("USR-001", Status.ACTIVE)).thenReturn(3L);
        assertThrows(LoanLimitExceededException.class,
                () -> loanService.createLoan("USR-001", "BOK-001"));
        verify(loanRepository, never()).save(any());
    }

    @Test
    @DisplayName("Crear préstamo lanza excepción cuando el usuario no existe")
    void createLoan_invalidUser_throwsException() {
        when(loanRepository.countByUserIdAndStatus(any(), any())).thenReturn(0L);
        when(userRepository.findById("INVALID")).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class,
                () -> loanService.createLoan("INVALID", "BOK-001"));
    }

    @Test
    @DisplayName("Crear préstamo lanza excepción cuando el libro no existe")
    void createLoan_invalidBook_throwsException() {
        when(loanRepository.countByUserIdAndStatus(any(), any())).thenReturn(0L);
        when(userRepository.findById("USR-001")).thenReturn(Optional.of(userEntity));
        when(bookRepository.findById("INVALID")).thenReturn(Optional.empty());
        assertThrows(BookNotFoundException.class,
                () -> loanService.createLoan("USR-001", "INVALID"));
    }

    @Test
    @DisplayName("Crear préstamo lanza excepción cuando no hay copias disponibles")
    void createLoan_noCopies_throwsException() {
        BookEntity sinCopias = new BookEntity("BOK-002", "Agotado", "Autor", 1, 0);
        when(loanRepository.countByUserIdAndStatus(any(), any())).thenReturn(0L);
        when(userRepository.findById("USR-001")).thenReturn(Optional.of(userEntity));
        when(bookRepository.findById("BOK-002")).thenReturn(Optional.of(sinCopias));
        assertThrows(BookNotAvailableException.class,
                () -> loanService.createLoan("USR-001", "BOK-002"));
    }

    // ---- returnBook ----

    @Test
    @DisplayName("Devolver libro cambia estado a RETURN y restaura copia")
    void returnBook_active_changesStatusAndRestoresCopy() {
        when(loanRepository.findById("LOAN-001")).thenReturn(Optional.of(activeLoanEntity));
        when(bookRepository.save(any())).thenReturn(bookEntity);
        when(loanRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        int copiasBefore = bookEntity.getAvailableCopies(); // 2
        Loan result = loanService.returnBook("LOAN-001");

        assertEquals(Status.RETURN, result.getStatus());
        assertNotNull(result.getReturnDate());
        assertEquals(copiasBefore + 1, bookEntity.getAvailableCopies()); // 3
        verify(loanRepository).save(any());
    }

    @Test
    @DisplayName("Devolver libro ya devuelto lanza excepción")
    void returnBook_alreadyReturned_throwsException() {
        activeLoanEntity.setStatus(Status.RETURN);
        when(loanRepository.findById("LOAN-001")).thenReturn(Optional.of(activeLoanEntity));
        assertThrows(IllegalArgumentException.class, () -> loanService.returnBook("LOAN-001"));
    }

    @Test
    @DisplayName("Devolver préstamo inexistente lanza LoanNotFoundException")
    void returnBook_nonExistent_throwsException() {
        when(loanRepository.findById("INVALID")).thenReturn(Optional.empty());
        assertThrows(LoanNotFoundException.class, () -> loanService.returnBook("INVALID"));
    }

    // ---- getLoanById ----

    @Test
    @DisplayName("getLoanById retorna préstamo existente")
    void getLoanById_existing_returnsLoan() {
        when(loanRepository.findById("LOAN-001")).thenReturn(Optional.of(activeLoanEntity));
        Loan loan = loanService.getLoanById("LOAN-001");
        assertEquals("LOAN-001", loan.getId());
    }

    @Test
    @DisplayName("getLoanById lanza LoanNotFoundException para ID inexistente")
    void getLoanById_nonExistent_throwsException() {
        when(loanRepository.findById("INVALID")).thenReturn(Optional.empty());
        assertThrows(LoanNotFoundException.class, () -> loanService.getLoanById("INVALID"));
    }

    // ---- getActiveLoansByUser ----

    @Test
    @DisplayName("getActiveLoansByUser retorna solo préstamos activos del usuario")
    void getActiveLoansByUser_returnsActiveOnly() {
        when(loanRepository.findByUserIdAndStatus("USR-001", Status.ACTIVE))
                .thenReturn(List.of(activeLoanEntity));
        List<Loan> loans = loanService.getActiveLoansByUser("USR-001");
        assertEquals(1, loans.size());
        assertEquals(Status.ACTIVE, loans.get(0).getStatus());
    }

    @Test
    @DisplayName("getActiveLoansByUser retorna lista vacía cuando no hay préstamos activos")
    void getActiveLoansByUser_noActive_returnsEmpty() {
        when(loanRepository.findByUserIdAndStatus("USR-001", Status.ACTIVE)).thenReturn(List.of());
        assertTrue(loanService.getActiveLoansByUser("USR-001").isEmpty());
    }
}