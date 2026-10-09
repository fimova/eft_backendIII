package com.duoc.cuentas_service.exception;

public class CuentaNotFoundException extends RuntimeException {

    public CuentaNotFoundException(String message) {
        super(message);
    }
}