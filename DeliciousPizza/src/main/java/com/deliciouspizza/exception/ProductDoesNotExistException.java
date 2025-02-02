package com.deliciouspizza.exception;

public class ProductDoesNotExistException extends RuntimeException {

    public ProductDoesNotExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProductDoesNotExistException(String message) {
        super(message);
    }

}
