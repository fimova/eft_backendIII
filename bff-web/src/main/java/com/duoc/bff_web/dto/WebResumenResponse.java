package com.duoc.bff_web.dto;

import java.util.List;

import lombok.Data;

@Data
public class WebResumenResponse {

    private WebCuentaResponse cuenta;
    private List<WebInteresResponse> intereses;
    private List<WebMovimientoResponse> movimientos;
    private List<WebTransaccionResponse> transacciones;
}