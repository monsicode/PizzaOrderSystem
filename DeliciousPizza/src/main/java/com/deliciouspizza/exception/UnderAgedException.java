package com.deliciouspizza.exception;

public class UnderAgedException extends Exception {

    public UnderAgedException(String message) {
        super(message);
    }

    public UnderAgedException(String message, Throwable cause) {
        super(message, cause);
    }

}