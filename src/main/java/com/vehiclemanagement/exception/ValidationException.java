package com.vehiclemanagement.exception;

/**
 * Excepción lanzada cuando hay una validación fallida en el negocio.
 */
public class ValidationException extends RuntimeException {
    
    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
