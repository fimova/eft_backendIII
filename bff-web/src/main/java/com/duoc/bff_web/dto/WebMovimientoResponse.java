package com.duoc.bff_web.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class WebMovimientoResponse {

    private LocalDate fecha;
    private String transaccion;
    private BigDecimal monto;
    private String descripcion;
}
