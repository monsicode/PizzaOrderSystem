package com.deliciouspizza.exception.runtime;

public class InvalildPriceException extends RuntimeException{
    public InvalildPriceException(String message) {
        super(message);
    }

    public InvalildPriceException(String message, Throwable cause) {
        super(message, cause);
    }
}
