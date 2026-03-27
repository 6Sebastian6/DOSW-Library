package edu.eci.dosw.tdd.core.validator;

public class LoanValidator {

    public static void validateUserId(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("El ID del usuario es obligatorio");
        }
    }

    public static void validateBookId(String bookId) {
        if (bookId == null || bookId.isBlank()) {
            throw new IllegalArgumentException("El ID del libro es obligatorio");
        }
    }
}

