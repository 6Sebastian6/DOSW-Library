package edu.eci.dosw.tdd.controller.mapper;

import edu.eci.dosw.tdd.controller.dto.LoanDTO;
import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.core.model.Status;

public class LoanMapper {
    public static LoanDTO toDTO(Loan loan) {
        if (loan == null) return null;

        return new LoanDTO(
                loan.getId(),
                loan.getUser() != null ? loan.getUser().getId() : null,
                loan.getBook() != null ? loan.getBook().getId() : null,
                loan.getLoanDate(),
                loan.getDueDate(),
                loan.getReturnDate(),
                loan.getStatus() != null ? loan.getStatus().name() : null
        );
    }

    public static Loan toEntity(LoanDTO loanDTO) {
        if (loanDTO == null) return null;

        Loan loan = new Loan();
        loan.setId(loanDTO.getId());
        loan.setLoanDate(loanDTO.getLoanDate());
        loan.setDueDate(loanDTO.getDueDate());
        loan.setReturnDate(loanDTO.getReturnDate());

        if (loanDTO.getStatus() != null) {
            loan.setStatus(Status.valueOf(loanDTO.getStatus()));
        }

        return loan;
    }
}
