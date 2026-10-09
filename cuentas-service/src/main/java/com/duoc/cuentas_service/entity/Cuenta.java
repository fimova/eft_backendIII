package com.duoc.cuentas_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cuentas")
@Getter
@Setter
@NoArgsConstructor
public class Cuenta {

    @Id
    @Column(name = "cuenta_id")
    private Long cuentaId;

    @Column(name = "estado", nullable = false, length = 20)
    private String estado;
}