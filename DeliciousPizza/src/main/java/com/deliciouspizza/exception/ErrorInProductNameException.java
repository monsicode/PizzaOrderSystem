package com.deliciouspizza.exception;

//to remove
public class ErrorInProductNameException extends Exception {
    public ErrorInProductNameException(String message) {
        super(message);
    }

    public ErrorInProductNameException(String message, Throwable cause) {
        super(message, cause);
    }
}
