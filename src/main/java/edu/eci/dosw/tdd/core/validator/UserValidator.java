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
    }
}

