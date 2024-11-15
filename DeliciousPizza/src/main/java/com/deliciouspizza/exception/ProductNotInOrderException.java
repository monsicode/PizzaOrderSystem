package com.deliciouspizza.exception;

public class ProductNotInOrderException extends Exception {
    public ProductNotInOrderException(String message) {
        super(message);
    }

    public ProductNotInOrderException(String message, Throwable cause) {
        super(message, cause);
    }
}
