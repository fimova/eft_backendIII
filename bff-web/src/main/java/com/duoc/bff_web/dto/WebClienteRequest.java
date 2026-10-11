package com.duoc.bff_web.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WebClienteRequest {

    private Long clienteId;
    private String nombre;
    private String email;
    private String estado;
}