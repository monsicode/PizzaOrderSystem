package com.deliciouspizza.exception;

public class ProductAlreadyDeactivated extends RuntimeException {
    public ProductAlreadyDeactivated(String message) {
        super(message);
    }

    public ProductAlreadyDeactivated(String message, Throwable cause) {
        super(message, cause);
    }
}
