package edu.eci.dosw.tdd.controller;

import edu.eci.dosw.tdd.controller.dto.LoanDTO;
import edu.eci.dosw.tdd.controller.mapper.LoanMapper;
import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.core.service.LoanService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @GetMapping
    public ResponseEntity<List<LoanDTO>> getAllLoans() {
        List<LoanDTO> loans = loanService.getAllLoans().stream()
                .map(LoanMapper::toDTO)
                .toList();
        return ResponseEntity.ok(loans);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LoanDTO> getLoanById(@PathVariable String id) {
        Loan loan = loanService.getLoanById(id);
        return ResponseEntity.ok(LoanMapper.toDTO(loan));
    }

    @PostMapping
    public ResponseEntity<LoanDTO> createLoan(@RequestParam String userId, @RequestParam String bookId) {
        Loan loan = loanService.createLoan(userId, bookId);
        return new ResponseEntity<>(LoanMapper.toDTO(loan), HttpStatus.CREATED);
    }

    @PutMapping("/{id}/return")
    public ResponseEntity<LoanDTO> returnBook(@PathVariable String id) {
        Loan loan = loanService.returnBook(id);
        return ResponseEntity.ok(LoanMapper.toDTO(loan));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<LoanDTO>> getActiveLoansByUser(@PathVariable String userId) {
        List<LoanDTO> loans = loanService.getActiveLoansByUser(userId).stream()
                .map(LoanMapper::toDTO)
                .toList();
        return ResponseEntity.ok(loans);
    }
}
