package com.duoc.clientes_service.exception;

public class ClienteAlreadyExistsException extends RuntimeException {

    public ClienteAlreadyExistsException(String message) {
        super(message);
    }
}
