package com.duoc.bff_web.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WebInteresResponse {

    private Long cuentaId;
    private BigDecimal saldoInicial;
    private BigDecimal interesAplicado;
    private BigDecimal saldoFinal;
    private String periodo;
    private String tipo;
}