package edu.eci.dosw.tdd.core.model;

import lombok.Data;
import lombok.AllArgsConstructor;// Este es para generar un constructor con los atributos de la clase
import lombok.NoArgsConstructor;// Esto genera un constructor vacio

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    private String Id;
    private String title;
    private String author;
    private int totalCopies;//Se agrego el total de copias
    private int availableCopies;//Se agrego para saber los disponibles
}
