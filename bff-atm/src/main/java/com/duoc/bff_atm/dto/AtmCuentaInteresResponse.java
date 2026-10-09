
package com.duoc.bff_atm.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class AtmCuentaInteresResponse {

    private Long cuentaId;
    private BigDecimal saldoInicial;
    private BigDecimal interesAplicado;
    private BigDecimal saldoFinal;
    private String periodo;
    private String tipo;
}