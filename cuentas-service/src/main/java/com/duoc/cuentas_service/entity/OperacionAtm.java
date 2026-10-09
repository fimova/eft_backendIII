package com.duoc.cuentas_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

//tabla para atm
@Entity
@Table(name = "operaciones_atm")
@Getter
@Setter
@NoArgsConstructor
public class OperacionAtm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "operacion_id")
    private Long operacionId;

    @Column(name = "cuenta_id", nullable = false)
    private Long cuentaId;

    @Column(name = "tipo", nullable = false, length = 20)
    private String tipo;

    @Column(name = "monto", nullable = false, precision = 15, scale = 2)
    private BigDecimal monto;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;
}
