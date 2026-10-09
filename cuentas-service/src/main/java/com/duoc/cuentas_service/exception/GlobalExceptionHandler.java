package com.duoc.cuentas_service.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CuentaNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleCuentaNotFound(
            CuentaNotFoundException ex) {

        Map<String, String> error = new HashMap<>();
        error.put("error", "Cuenta no encontrada");
        error.put("mensaje", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(error);
    }

    @ExceptionHandler(CuentaAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleCuentaAlreadyExists(
            CuentaAlreadyExistsException ex) {

        Map<String, String> error = new HashMap<>();
        error.put("error", "Cuenta ya existe");
        error.put("mensaje", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(
            MethodArgumentNotValidException ex) {

        Map<String, String> errores = new HashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error -> errores.put(error.getField(), error.getDefaultMessage()));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errores);
    }
}