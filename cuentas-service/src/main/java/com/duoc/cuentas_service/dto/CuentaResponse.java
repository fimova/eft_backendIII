package com.duoc.cuentas_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CuentaResponse {

    private Long cuentaId;
    private String estado;
}