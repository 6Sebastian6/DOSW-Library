package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.model.*;
import edu.eci.dosw.tdd.core.util.DateUtil;
import edu.eci.dosw.tdd.core.util.IdGeneratorUtil;
import edu.eci.dosw.tdd.exception.BookNotAvailableException;
import edu.eci.dosw.tdd.exception.LoanLimitExceededException;
import edu.eci.dosw.tdd.exception.LoanNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class LoanService {

    private final List<Loan> loans = new ArrayList<>();
    private final BookService bookService;
    private final UserService userService;

    public LoanService(BookService bookService, UserService userService) {
        this.bookService = bookService;
        this.userService = userService;
    }

    public List<Loan> getAllLoans() {
        return new ArrayList<>(loans);
    }

    public Loan getLoanById(String id) {
        return loans.stream()
                .filter(loan -> loan.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new LoanNotFoundException("Préstamo no encontrado con ID: " + id));
    }

    public Loan createLoan(String userId, String bookId) {
        long activeLoans = loans.stream()
                .filter(loan -> loan.getUser().getId().equals(userId) && loan.getStatus() == Status.ACTIVE)
                .count();

        if (activeLoans >= 3) {
            throw new LoanLimitExceededException("El usuario ya tiene 3 préstamos activos");
        }

        User user = userService.getUserById(userId);
        Book book = bookService.getBookById(bookId);

        if (book.getAvailableCopies() <= 0) {
            throw new BookNotAvailableException("No hay ejemplares disponibles del libro: " + book.getTitle());
        }

        Loan loan = new Loan();
        loan.setId(IdGeneratorUtil.generateLoanId());
        loan.setUser(user);
        loan.setBook(book);
        loan.setLoanDate(LocalDate.now());
        loan.setDueDate(DateUtil.calculateDueDate(7));
        loan.setStatus(Status.ACTIVE);

        book.setAvailableCopies(book.getAvailableCopies() - 1);

        loans.add(loan);
        return loan;
    }

    public Loan returnBook(String loanId) {
        Loan loan = getLoanById(loanId);

        if (loan.getStatus() == Status.RETURN) {
            throw new IllegalArgumentException("El libro ya fue devuelto");
        }

        loan.setReturnDate(LocalDate.now());
        loan.setStatus(Status.RETURN);

        Book book = loan.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);

        return loan;
    }

    public List<Loan> getActiveLoansByUser(String userId) {
        return loans.stream()
                .filter(loan -> loan.getUser().getId().equals(userId) && loan.getStatus() == Status.ACTIVE)
                .toList();
    }

    public List<Loan> getLoansByBook(String bookId) {
        return loans.stream()
                .filter(loan -> loan.getBook().getId().equals(bookId))
                .toList();
    }
}