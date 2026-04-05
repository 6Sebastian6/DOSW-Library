package edu.eci.dosw.tdd.controller.dto;

import edu.eci.dosw.tdd.core.model.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private String id;

    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    @NotBlank(message = "El nombre de usuario es obligatorio")
    private String username;

    // Solo recibe en la cracion, pero nunca devuelve en la respuesta
    private String password;

    @NotNull(message = "El rol es obligatorio (USER o LIBRARIAN)")
    private Role role;
}

