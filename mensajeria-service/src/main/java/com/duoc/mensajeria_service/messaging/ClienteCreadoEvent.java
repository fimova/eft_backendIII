package com.duoc.mensajeria_service.messaging;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteCreadoEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long clienteId;
    private LocalDateTime fecha;
    private String tipo;
}
