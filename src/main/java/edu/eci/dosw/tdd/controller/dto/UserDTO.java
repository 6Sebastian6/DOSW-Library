package edu.eci.dosw.tdd.controller.dto;

import lombok.Data;
import lombok.AllArgsConstructor;// Este es para generar un constructor con los atributos de la clase
import lombok.NoArgsConstructor;// Esto genera un constructor vacio

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String id;
    private String name;
}
