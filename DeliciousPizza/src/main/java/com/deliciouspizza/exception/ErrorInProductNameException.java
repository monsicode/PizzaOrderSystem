package com.deliciouspizza.exception;

public class ErrorInProductNameException extends Exception {
    public ErrorInProductNameException(String message) {
        super(message);
    }

    public ErrorInProductNameException(String message, Throwable cause) {
        super(message, cause);
    }
}
