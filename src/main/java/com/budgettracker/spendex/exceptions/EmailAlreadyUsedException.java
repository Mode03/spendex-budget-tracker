package com.budgettracker.spendex.exceptions;

public class EmailAlreadyUsedException extends RuntimeException {
    public EmailAlreadyUsedException(String msg) {
        super(msg);
    }
}
