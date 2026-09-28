package com.restaurant.exception;

public class InvalidVisitStateException extends RuntimeException {
    public InvalidVisitStateException(String message) {
        super(message);
    }
}
