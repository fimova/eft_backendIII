package com.duoc.cuentas_service.messaging;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//representa deposito y transferencia
//depósito: cuentaOrigenId será null y cuentaDestinoId tendrá la cuenta que recibe el dinero
//transferencia: ambos identificadores tendrán valor
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperacionCuentaEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long operacionId;
    private Long cuentaOrigenId;
    private Long cuentaDestinoId;
    private BigDecimal monto;
    private LocalDateTime fecha;
    private String tipo;
}