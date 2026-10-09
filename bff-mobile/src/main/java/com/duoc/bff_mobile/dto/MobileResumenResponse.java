package com.duoc.bff_mobile.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class MobileResumenResponse {
    private MobileCuentaResponse cuenta;
    private BigDecimal interesAplicado;
    private String periodo;
    private List<MobileMovimientoResponse> movimientos;
}