package edu.eci.dosw.tdd.core.util;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class DateUtil {

    public static LocalDate calculateDueDate(int loanDays) {
        return LocalDate.now().plusDays(loanDays);
    }

    public static long daysBetween(LocalDate start, LocalDate end) {
        return ChronoUnit.DAYS.between(start, end);
    }

}
