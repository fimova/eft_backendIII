package com.duoc.cuentas_service.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TransferenciaRequest {

    @NotNull
    private Long cuentaDestinoId;

    @NotNull
    @Positive
    private BigDecimal monto;
}