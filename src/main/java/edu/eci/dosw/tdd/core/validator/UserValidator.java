package edu.eci.dosw.tdd.core.validator;

import edu.eci.dosw.tdd.core.model.User;

public class UserValidator {

    public static void validate(User user) {
        if (user == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            throw new IllegalArgumentException("El nombre del usuario es obligatorio");
        }
        if (user.getUsername() == null || user.getUsername().isBlank()) {
            throw new IllegalArgumentException("El nombre de usuario (username) es obligatorio");
        }
        if (user.getRole() == null) {
            throw new IllegalArgumentException("El rol del usuario es obligatorio (USER o LIBRARIAN)");
        }
    }

    // Validacion para creacion, ya que el password solo se necesita al crear
    public static void validateForCreate(User user) {
        validate(user);
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new IllegalArgumentException("La contraseña es obligatoria");
        }
    }
}
