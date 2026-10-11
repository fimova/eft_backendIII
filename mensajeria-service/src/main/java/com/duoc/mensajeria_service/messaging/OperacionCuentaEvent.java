package com.duoc.mensajeria_service.messaging;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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