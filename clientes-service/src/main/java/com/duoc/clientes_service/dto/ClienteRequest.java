package com.duoc.clientes_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClienteRequest {

    @NotNull(message = "El ID del cliente es obligatorio")
    @Positive(message = "El ID del cliente debe ser mayor que cero")
    private Long clienteId;

    @NotBlank(message = "El nombre del cliente es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    private String nombre;

    @NotBlank(message = "El email del cliente es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    @Size(max = 150, message = "El email no puede superar los 150 caracteres")
    private String email;

    @NotBlank(message = "El estado del cliente es obligatorio")
    @Size(max = 20, message = "El estado no puede superar los 20 caracteres")
    private String estado;
}
