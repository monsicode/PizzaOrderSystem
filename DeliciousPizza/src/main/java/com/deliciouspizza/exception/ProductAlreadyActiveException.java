package com.deliciouspizza.exception;

public class ProductAlreadyActiveException extends Exception  {
    public ProductAlreadyActiveException(String message) {
        super(message);
    }

    public ProductAlreadyActiveException(String message, Throwable cause) {
        super(message, cause);
    }
}
