package edu.eci.dosw.tdd.core.util;

public class ValidationUtil {

    public static void requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("El campo '" + fieldName + "' es obligatorio");
        }
    }

    public static void requirePositive(int value, String fieldName) {
        if (value <= 0) {
            throw new IllegalArgumentException("El campo '" + fieldName + "' debe ser mayor a cero");
        }
    }

    public static void requireNonNull(Object value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException("El campo '" + fieldName + "' no puede ser nulo");
        }
    }
}
