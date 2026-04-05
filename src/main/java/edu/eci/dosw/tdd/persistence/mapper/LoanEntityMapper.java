package edu.eci.dosw.tdd.persistence.mapper;

import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.persistence.entity.BookEntity;
import edu.eci.dosw.tdd.persistence.entity.LoanEntity;
import edu.eci.dosw.tdd.persistence.entity.UserEntity;

public class LoanEntityMapper {

    private LoanEntityMapper() {}

    public static Loan toDomain(LoanEntity entity) {
        if (entity == null) return null;
        return new Loan(
                entity.getId(),
                BookEntityMapper.toDomain(entity.getBook()),
                UserEntityMapper.toDomain(entity.getUser()),
                entity.getLoanDate(),
                entity.getDueDate(),
                entity.getStatus(),
                entity.getReturnDate()
        );
    }

    public static LoanEntity toEntity(Loan loan, BookEntity bookEntity, UserEntity userEntity) {
        if (loan == null) return null;
        return LoanEntity.builder()
                .id(loan.getId())
                .book(bookEntity)
                .user(userEntity)
                .loanDate(loan.getLoanDate())
                .dueDate(loan.getDueDate())
                .status(loan.getStatus())
                .returnDate(loan.getReturnDate())
                .build();
    }
}
