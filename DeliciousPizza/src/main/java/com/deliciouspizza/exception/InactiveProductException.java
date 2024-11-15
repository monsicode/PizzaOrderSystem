package com.deliciouspizza.exception;

public class InactiveProductException extends Exception {
    public InactiveProductException(String message) {
        super(message);
    }

    public InactiveProductException(String message, Throwable cause) {
        super(message, cause);
    }
}
