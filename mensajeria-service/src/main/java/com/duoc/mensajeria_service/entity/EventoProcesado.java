package com.duoc.mensajeria_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

//se adapta para los nuevos MS también
@Entity
@Table(name = "eventos_procesados")
@Getter
@Setter
@NoArgsConstructor
public class EventoProcesado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ID de la transacción, cuando corresponda al evento
    @Column(name = "transaccion_id")
    private Long transaccionId;

    // ID del evento origen
    @Column(name = "referencia_id")
    private Long referenciaId;

    // MS que originó el evento
    @Column(name = "servicio_origen", nullable = false, length = 50)
    private String servicioOrigen;

    // tipo de evento procesado
    @Column(name = "tipo_evento", nullable = false, length = 50)
    private String tipoEvento;

    @Column(name = "fecha_procesamiento", nullable = false)
    private LocalDateTime fechaProcesamiento;

    @Column(name = "estado", nullable = false, length = 20)
    private String estado;

    @Column(name = "detalle", length = 500)
    private String detalle;
}
