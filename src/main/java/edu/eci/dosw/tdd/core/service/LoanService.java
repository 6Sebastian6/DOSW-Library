package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.core.model.Status;
import edu.eci.dosw.tdd.core.util.DateUtil;
import edu.eci.dosw.tdd.core.util.IdGeneratorUtil;
import edu.eci.dosw.tdd.exception.BookNotAvailableException;
import edu.eci.dosw.tdd.exception.BookNotFoundException;
import edu.eci.dosw.tdd.exception.LoanLimitExceededException;
import edu.eci.dosw.tdd.exception.LoanNotFoundException;
import edu.eci.dosw.tdd.exception.UserNotFoundException;
import edu.eci.dosw.tdd.persistence.entity.BookEntity;
import edu.eci.dosw.tdd.persistence.entity.LoanEntity;
import edu.eci.dosw.tdd.persistence.entity.UserEntity;
import edu.eci.dosw.tdd.persistence.mapper.LoanEntityMapper;
import edu.eci.dosw.tdd.persistence.repository.BookRepository;
import edu.eci.dosw.tdd.persistence.repository.LoanRepository;
import edu.eci.dosw.tdd.persistence.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class LoanService {

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public LoanService(LoanRepository loanRepository,
                       BookRepository bookRepository,
                       UserRepository userRepository) {
        this.loanRepository = loanRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    public List<Loan> getAllLoans() {
        return loanRepository.findAll().stream()
                .map(LoanEntityMapper::toDomain)
                .toList();
    }

    public Loan getLoanById(String id) {
        return loanRepository.findById(id)
                .map(LoanEntityMapper::toDomain)
                .orElseThrow(() -> new LoanNotFoundException("Préstamo no encontrado con ID: " + id));
    }

    @Transactional
    public Loan createLoan(String userId, String bookId) {
        //Se ve el limite de 3 prestamos activos
        long activeLoans = loanRepository.countByUserIdAndStatus(userId, Status.ACTIVE);
        if (activeLoans >= 3) {
            throw new LoanLimitExceededException("El usuario ya tiene 3 préstamos activos");
        }

        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con ID: " + userId));

        BookEntity bookEntity = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Libro no encontrado con ID: " + bookId));

        if (bookEntity.getAvailableCopies() <= 0) {
            throw new BookNotAvailableException(
                    "No hay ejemplares disponibles del libro: " + bookEntity.getTitle());
        }

        bookEntity.setAvailableCopies(bookEntity.getAvailableCopies() - 1);
        bookRepository.save(bookEntity);

        LoanEntity loanEntity = LoanEntity.builder()
                .id(IdGeneratorUtil.generateLoanId())
                .user(userEntity)
                .book(bookEntity)
                .loanDate(LocalDate.now())
                .dueDate(DateUtil.calculateDueDate(7))
                .status(Status.ACTIVE)
                .build();

        LoanEntity saved = loanRepository.save(loanEntity);
        return LoanEntityMapper.toDomain(saved);
    }

    @Transactional
    public Loan returnBook(String loanId) {
        LoanEntity loanEntity = loanRepository.findById(loanId)
                .orElseThrow(() -> new LoanNotFoundException("Préstamo no encontrado con ID: " + loanId));

        if (loanEntity.getStatus() == Status.RETURN) {
            throw new IllegalArgumentException("El libro ya fue devuelto");
        }

        // Restaura la copia disponible y la guarda en BD
        BookEntity bookEntity = loanEntity.getBook();
        bookEntity.setAvailableCopies(bookEntity.getAvailableCopies() + 1);
        bookRepository.save(bookEntity);

        // Actualiza el prestamo
        loanEntity.setStatus(Status.RETURN);
        loanEntity.setReturnDate(LocalDate.now());

        return LoanEntityMapper.toDomain(loanRepository.save(loanEntity));
    }

    public List<Loan> getActiveLoansByUser(String userId) {
        return loanRepository.findByUserIdAndStatus(userId, Status.ACTIVE).stream()
                .map(LoanEntityMapper::toDomain)
                .toList();
    }

    public List<Loan> getLoansByBook(String bookId) {
        return loanRepository.findByBookId(bookId).stream()
                .map(LoanEntityMapper::toDomain)
                .toList();
    }
}