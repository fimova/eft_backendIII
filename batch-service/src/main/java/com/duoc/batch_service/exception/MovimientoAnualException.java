package com.duoc.batch_service.exception;

public class MovimientoAnualException extends RuntimeException{

    public MovimientoAnualException (String message){
        super(message);
    }

    public MovimientoAnualException(String message, Throwable cause){
        super(message, cause);
    }
    
}
