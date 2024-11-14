package com.deliciouspizza.exception;

public class ProductAlreadyDeactivatedException extends Exception {
    public ProductAlreadyDeactivatedException(String message) {
        super(message);
    }

    public ProductAlreadyDeactivatedException(String message, Throwable cause) {
        super(message, cause);
    }
}
