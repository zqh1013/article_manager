package com.example.registration.exception.exception;

public class RepeatedEmailException extends RuntimeException {
    public RepeatedEmailException(String message) {
        super(message);
    }
}
