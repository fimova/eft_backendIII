
package com.duoc.cuentas_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class RetiroAtmResponse {

    private Long cuentaId;
    private BigDecimal montoRetirado;
    private BigDecimal saldoDisponible;
    private String mensaje;
}
