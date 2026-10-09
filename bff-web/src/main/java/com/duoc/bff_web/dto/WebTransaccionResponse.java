package com.duoc.bff_web.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class WebTransaccionResponse {

    private Long transaccionId;
    private LocalDate fecha;
    private BigDecimal monto;
    private String tipo;
    private boolean anomalia;
    private String motivoAnomalia;
}
