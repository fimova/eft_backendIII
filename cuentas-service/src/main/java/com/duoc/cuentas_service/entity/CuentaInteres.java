package com.duoc.cuentas_service.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cuentas_intereses")
@Getter
@Setter
@NoArgsConstructor
public class CuentaInteres {

    @Id
    @Column(name = "cuenta_interes_id")
    private Long cuentaInteresId;

    @Column(name = "cuenta_id", nullable = false)
    private Long cuentaId;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "saldo_inicial", nullable = false, precision = 15, scale = 2)
    private BigDecimal saldoInicial;

    @Column(name = "tipo", nullable = false, length = 20)
    private String tipo;

    @Column(name = "interes_aplicado", nullable = false, precision = 15, scale = 2)
    private BigDecimal interesAplicado;

    @Column(name = "saldo_final", nullable = false, precision = 15, scale = 2)
    private BigDecimal saldoFinal;

    @Column(name = "periodo", nullable = false, length = 7)
    private String periodo;

    @Column(name = "edad", nullable = false)
    private Integer edad;
}