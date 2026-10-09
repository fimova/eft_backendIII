package com.duoc.cuentas_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CuentaRequest {

    @NotNull(message = "El ID de la cuenta es obligatorio")
    @Positive(message = "El ID de la cuenta debe ser mayor que cero")
    private Long cuentaId;

    @NotBlank(message = "El estado de la cuenta es obligatorio")
    @Size(max = 20, message = "El estado no puede superar los 20 caracteres")
    private String estado;
}
