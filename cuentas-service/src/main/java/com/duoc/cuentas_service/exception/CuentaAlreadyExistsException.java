package com.duoc.cuentas_service.exception;

public class CuentaAlreadyExistsException extends RuntimeException {

    public CuentaAlreadyExistsException(String message) {
        super(message);
    }
}