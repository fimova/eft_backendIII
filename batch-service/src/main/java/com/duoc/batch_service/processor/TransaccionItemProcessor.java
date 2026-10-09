package com.duoc.batch_service.processor;

import java.math.BigDecimal;

import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.duoc.batch_service.exception.TransaccionException;
import com.duoc.batch_service.model.Transaccion;

@Component
public class TransaccionItemProcessor implements ItemProcessor<Transaccion, Transaccion> {

    @Override
    public Transaccion process(Transaccion transaccion) {

        // Validación de datos
        if (transaccion.getTransaccionId() == null ||
            transaccion.getTransaccionId() <= 0) {

            throw new TransaccionException(
                "El ID de la transacción es obligatorio y debe ser mayor a cero."
            );
        }

        if (transaccion.getFecha() == null) {
            throw new TransaccionException(
                "La fecha es obligatoria."
            );
        }

        if (transaccion.getMonto() == null) {
            throw new TransaccionException(
                "El monto es obligatorio."
            );
        }

        if (transaccion.getTipo() == null ||
            transaccion.getTipo().isBlank()) {

            throw new TransaccionException(
                "El tipo de transacción es obligatorio."
            );
        }

        // Normalización
        transaccion.setTipo(
            transaccion.getTipo().trim().toLowerCase()
        );

        // Validación del tipo
        if (!transaccion.getTipo().equals("debito") &&
            !transaccion.getTipo().equals("credito")) {

            throw new TransaccionException(
                "El tipo de transacción debe ser debito o credito."
            );
        }

        // Detección de anomalías. En este caso no se hará skip/exception en el writer, para dejar un registro
        if (transaccion.getMonto().compareTo(BigDecimal.ZERO) <= 0) {

            transaccion.setAnomalia(true);

            transaccion.setMotivoAnomalia(
                "El monto debe ser mayor a cero."
            );

        } else {

            transaccion.setAnomalia(false);
            transaccion.setMotivoAnomalia(null);
        }

        return transaccion;
    }
}
