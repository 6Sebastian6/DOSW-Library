package edu.eci.dosw.tdd.service;

import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.core.model.Status;
import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.core.service.BookService;
import edu.eci.dosw.tdd.core.service.LoanService;
import edu.eci.dosw.tdd.core.service.UserService;
import edu.eci.dosw.tdd.exception.BookNotAvailableException;
import edu.eci.dosw.tdd.exception.LoanLimitExceededException;
import edu.eci.dosw.tdd.exception.LoanNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("LoanService - Pruebas unitarias")
class LoanServiceTest {

    private BookService bookService;
    private UserService userService;
    private LoanService loanService;

    private User testUser;
    private Book testBook;

    @BeforeEach
    void setUp() {
        bookService = new BookService();
        userService = new UserService();
        loanService = new LoanService(bookService, userService);

        testUser = userService.createUser(new User("Juan Pérez", null));
        testBook = bookService.createBook(new Book(null, "Clean Code", "Martin", 3, 0));
    }

    // ---- createLoan ----

    @Test
    @DisplayName("Crear préstamo válido establece estado ACTIVE y reduce copias disponibles")
    void createLoan_validData_createsActiveLoan() {
        int copiesBefore = testBook.getAvailableCopies();
        Loan loan = loanService.createLoan(testUser.getId(), testBook.getId());

        assertNotNull(loan.getId());
        assertTrue(loan.getId().startsWith("LOAN-"));
        assertEquals(Status.ACTIVE, loan.getStatus());
        assertNotNull(loan.getLoanDate());
        assertNotNull(loan.getDueDate());
        assertEquals(copiesBefore - 1, testBook.getAvailableCopies());
    }

    @Test
    @DisplayName("Crear préstamo lanza excepción cuando el usuario tiene 3 activos")
    void createLoan_userHas3ActiveLoans_throwsLoanLimitExceededException() {
        Book b1 = bookService.createBook(new Book(null, "Libro 1", "Autor", 1, 0));
        Book b2 = bookService.createBook(new Book(null, "Libro 2", "Autor", 1, 0));
        Book b3 = bookService.createBook(new Book(null, "Libro 3", "Autor", 1, 0));

        loanService.createLoan(testUser.getId(), b1.getId());
        loanService.createLoan(testUser.getId(), b2.getId());
        loanService.createLoan(testUser.getId(), b3.getId());

        assertThrows(LoanLimitExceededException.class,
                () -> loanService.createLoan(testUser.getId(), testBook.getId()));
    }

    @Test
    @DisplayName("Crear préstamo lanza excepción cuando no hay copias disponibles")
    void createLoan_noAvailableCopies_throwsBookNotAvailableException() {
        Book soloUnaCopia = bookService.createBook(new Book(null, "Un solo ejemplar", "Autor", 1, 0));
        loanService.createLoan(testUser.getId(), soloUnaCopia.getId());

        User otroUsuario = userService.createUser(new User("Otro", null));
        assertThrows(BookNotAvailableException.class,
                () -> loanService.createLoan(otroUsuario.getId(), soloUnaCopia.getId()));
    }

    // ---- returnBook ----

    @Test
    @DisplayName("Devolver libro cambia estado a RETURN y restaura copia")
    void returnBook_activeLoan_changesStatusAndRestoresCopy() {
        Loan loan = loanService.createLoan(testUser.getId(), testBook.getId());
        int copiesAfterLoan = testBook.getAvailableCopies();

        Loan returned = loanService.returnBook(loan.getId());

        assertEquals(Status.RETURN, returned.getStatus());
        assertNotNull(returned.getReturnDate());
        assertEquals(copiesAfterLoan + 1, testBook.getAvailableCopies());
    }

    @Test
    @DisplayName("Devolver un libro ya devuelto lanza excepción")
    void returnBook_alreadyReturned_throwsException() {
        Loan loan = loanService.createLoan(testUser.getId(), testBook.getId());
        loanService.returnBook(loan.getId());

        assertThrows(IllegalArgumentException.class, () -> loanService.returnBook(loan.getId()));
    }

    @Test
    @DisplayName("Devolver préstamo con ID inexistente lanza LoanNotFoundException")
    void returnBook_nonExistentId_throwsLoanNotFoundException() {
        assertThrows(LoanNotFoundException.class, () -> loanService.returnBook("INVALID"));
    }

    // ---- getLoanById ----

    @Test
    @DisplayName("getLoanById retorna préstamo existente")
    void getLoanById_existingId_returnsLoan() {
        Loan created = loanService.createLoan(testUser.getId(), testBook.getId());
        Loan found = loanService.getLoanById(created.getId());
        assertEquals(created.getId(), found.getId());
    }

    @Test
    @DisplayName("getLoanById con ID inválido lanza LoanNotFoundException")
    void getLoanById_nonExistentId_throwsException() {
        assertThrows(LoanNotFoundException.class, () -> loanService.getLoanById("INVALID"));
    }

    // ---- getAllLoans ----

    @Test
    @DisplayName("getAllLoans retorna todos los préstamos creados")
    void getAllLoans_returnsAllLoans() {
        Book otroLibro = bookService.createBook(new Book(null, "Otro Libro", "Autor", 1, 0));
        loanService.createLoan(testUser.getId(), testBook.getId());
        loanService.createLoan(testUser.getId(), otroLibro.getId());
        assertEquals(2, loanService.getAllLoans().size());
    }

    // ---- getActiveLoansByUser ----

    @Test
    @DisplayName("getActiveLoansByUser retorna solo préstamos activos del usuario")
    void getActiveLoansByUser_returnsOnlyActiveLoans() {
        Book b2 = bookService.createBook(new Book(null, "Libro 2", "Autor", 1, 0));
        Loan loan1 = loanService.createLoan(testUser.getId(), testBook.getId());
        loanService.createLoan(testUser.getId(), b2.getId());

        loanService.returnBook(loan1.getId());

        List<Loan> activeLoans = loanService.getActiveLoansByUser(testUser.getId());
        assertEquals(1, activeLoans.size());
        assertEquals(Status.ACTIVE, activeLoans.get(0).getStatus());
    }

    @Test
    @DisplayName("getActiveLoansByUser no retorna préstamos de otros usuarios")
    void getActiveLoansByUser_doesNotReturnOtherUsersLoans() {
        User otroUsuario = userService.createUser(new User("Otro Usuario", null));
        Book otroLibro = bookService.createBook(new Book(null, "Otro Libro", "Autor", 1, 0));

        loanService.createLoan(testUser.getId(), testBook.getId());
        loanService.createLoan(otroUsuario.getId(), otroLibro.getId());

        List<Loan> loansDelUsuario = loanService.getActiveLoansByUser(testUser.getId());
        assertEquals(1, loansDelUsuario.size());
        assertEquals(testUser.getId(), loansDelUsuario.get(0).getUser().getId());
    }

    // ---- límite devuelto no cuenta ----

    @Test
    @DisplayName("Préstamo devuelto no bloquea nuevo préstamo al mismo usuario")
    void createLoan_afterReturn_allowsNewLoan() {
        Book b1 = bookService.createBook(new Book(null, "L1", "A", 1, 0));
        Book b2 = bookService.createBook(new Book(null, "L2", "A", 1, 0));
        Book b3 = bookService.createBook(new Book(null, "L3", "A", 1, 0));
        Book b4 = bookService.createBook(new Book(null, "L4", "A", 1, 0));

        Loan l1 = loanService.createLoan(testUser.getId(), b1.getId());
        loanService.createLoan(testUser.getId(), b2.getId());
        loanService.createLoan(testUser.getId(), b3.getId());

        loanService.returnBook(l1.getId());

        Loan nuevo = loanService.createLoan(testUser.getId(), b4.getId());
        assertNotNull(nuevo);
        assertEquals(Status.ACTIVE, nuevo.getStatus());
    }
}