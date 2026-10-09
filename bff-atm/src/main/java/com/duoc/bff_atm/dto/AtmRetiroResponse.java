package com.duoc.bff_atm.dto;

import lombok.Data;

import java.math.BigDecimal;

//mismos campos que devuelve RetiroAtmResponse en cuentas-service para que Feign
// pueda convertir la respuesta automáticamente
@Data
public class AtmRetiroResponse {

    private Long cuentaId;
    private BigDecimal montoRetirado;
    private BigDecimal saldoDisponible;
    private String mensaje;
}
