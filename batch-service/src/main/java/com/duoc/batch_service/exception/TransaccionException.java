package com.duoc.batch_service.exception;

public class TransaccionException extends RuntimeException {

    public TransaccionException (String message){
        super(message);
    }

    public TransaccionException (String message, Throwable cause){
        super(message, cause);
    }
    
}
