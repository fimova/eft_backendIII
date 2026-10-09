package com.duoc.bff_mobile.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MobileInteresResponse {

    private Long cuentaId;
    private BigDecimal saldoInicial;
    private BigDecimal interesAplicado;
    private BigDecimal saldoFinal;
    private String periodo;
    private String tipo;
}
