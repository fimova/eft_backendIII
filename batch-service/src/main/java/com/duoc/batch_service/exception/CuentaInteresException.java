package com.duoc.batch_service.exception;

public class CuentaInteresException extends RuntimeException{

    public CuentaInteresException (String message) {
        super (message);
    }

    public CuentaInteresException (String message, Throwable cause){
        super(message, cause);
    }
    
}
