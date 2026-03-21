package edu.eci.dosw.tdd.controller.dto;


import java.time.LocalDate;
import lombok.Data;
import lombok.AllArgsConstructor;// Este es para generar un constructor con los atributos de la clase
import lombok.NoArgsConstructor;// Esto genera un constructor vacio

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanDTO {

    private String id;
    private String userId;
    private String bookId;
    private LocalDate loanDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private String status;
}
