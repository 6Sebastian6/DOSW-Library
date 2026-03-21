package edu.eci.dosw.tdd.exception;

public class LoanLimitExceededException extends RuntimeException{
    public LoanLimitExceededException(String message) {
        super(message);
    }
}
