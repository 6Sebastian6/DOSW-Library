package edu.eci.dosw.tdd.core.util;

import java.util.UUID;

public class IdGeneratorUtil {

    public static String generateUserId() {
        return "USR-" + UUID.randomUUID().toString().substring(0, 8);
    }

    public static String generateBookId() {
        return "BOK-" + UUID.randomUUID().toString().substring(0, 8);
    }

    public static String generateLoanId() {
        return "LOAN-" + UUID.randomUUID().toString().substring(0, 8);
    }

}
