package com.duoc.cuentas_service.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.domain.Persistable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "operaciones_cuenta")
@Getter
@Setter
@NoArgsConstructor
public class OperacionCuenta implements Persistable<Long> {

    @Transient
    private boolean nueva = true;

    @Override
    public Long getId() {
        return operacionId;
    }

    @Override
    public boolean isNew() {
        return nueva;
    }

    @PostPersist
    @PostUpdate
    private void marcarComoExistente() {
        nueva = false;
    }

    @Id
    @Column(name = "operacion_id")
    private Long operacionId;

    @Column(name = "cuenta_origen_id")
    private Long cuentaOrigenId;

    @Column(name = "cuenta_destino_id")
    private Long cuentaDestinoId;

    @Column(name = "tipo", nullable = false, length = 20)
    private String tipo;

    @Column(name = "monto", nullable = false, precision = 15, scale = 2)
    private BigDecimal monto;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;
}