package edu.eci.dosw.tdd.core.model;

import java.time.LocalDate;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Loan {
    private String id;
    private Book book;
    private User user;
    private LocalDate loanDate;
    private LocalDate dueDate;
    private Status status;
    private LocalDate returnDate;

}
