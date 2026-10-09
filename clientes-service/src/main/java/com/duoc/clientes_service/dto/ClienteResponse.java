package com.duoc.clientes_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ClienteResponse {

    private Long clienteId;
    private String nombre;
    private String email;
    private String estado;
}