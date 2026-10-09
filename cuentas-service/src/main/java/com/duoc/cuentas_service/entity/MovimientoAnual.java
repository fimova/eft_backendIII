package com.duoc.cuentas_service.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "movimientos_anuales")
@Getter
@Setter
@NoArgsConstructor
public class MovimientoAnual {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movimiento_id")
    private Long movimientoId;

    @Column(name = "cuenta_id", nullable = false)
    private Long cuentaId;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "transaccion", nullable = false, length = 20)
    private String transaccion;

    @Column(name = "monto", nullable = false, precision = 15, scale = 2)
    private BigDecimal monto;

    @Column(name = "descripcion", nullable = false, length = 200)
    private String descripcion;
}
