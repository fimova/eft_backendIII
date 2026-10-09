package com.duoc.bff_mobile.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class MobileMovimientoResponse {
    private LocalDate fecha;
    private String transaccion;
    private BigDecimal monto;
    private String descripcion;
}