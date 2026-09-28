package com.restaurant.exception;

public class TableAlreadyOccupiedException extends RuntimeException {
    public TableAlreadyOccupiedException(String message) {
        super(message);
    }
}
