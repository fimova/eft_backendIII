package com.duoc.batch_service.processor;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.duoc.batch_service.exception.CuentaInteresException;
import com.duoc.batch_service.model.CuentaInteres;

@Component
public class CuentaInteresItemProcessor
        implements ItemProcessor<CuentaInteres, CuentaInteres> {

    @Value("${app.intereses.periodo}")
    private String periodo;

    @Override
    public CuentaInteres process(CuentaInteres cuenta) {

        // Validaciones
        if (cuenta.getCuentaId() == null || cuenta.getCuentaId() <= 0) {
            throw new CuentaInteresException(
                    "La cuentaId es obligatoria y mayor a cero.");
        }

        if (cuenta.getNombre() == null || cuenta.getNombre().isBlank() 
            || cuenta.getNombre().trim().equalsIgnoreCase("unknown")) {
            throw new CuentaInteresException(
                    "El nombre es obligatorio.");
        }

        if (cuenta.getSaldo() == null || cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0) {
            throw new CuentaInteresException(
                    "El saldo es obligatorio y no puede ser negativo.");
        }

        if (cuenta.getEdad() == null || cuenta.getEdad() <= 0) {
            throw new CuentaInteresException(
                    "La edad es obligatoria y debe ser mayor a cero.");
        }

        if (cuenta.getTipo() == null || cuenta.getTipo().isBlank()) {
            throw new CuentaInteresException(
                    "El tipo de cuenta es obligatorio.");
        }

        // Normalización
        cuenta.setNombre(cuenta.getNombre().trim());
        cuenta.setTipo(cuenta.getTipo().trim().toLowerCase());

        // Asignar período del procesamiento
        cuenta.setPeriodo(periodo);

        // Validación del tipo
        if (!cuenta.getTipo().equals("ahorro")
                && !cuenta.getTipo().equals("prestamo")
                && !cuenta.getTipo().equals("hipoteca")) {

            throw new CuentaInteresException(
                    "El tipo de cuenta no es válido.");
        }

        // Determinar tasa de interés
        BigDecimal tasa;

        switch (cuenta.getTipo()) {
            case "ahorro":
                tasa = new BigDecimal("0.02");
                break;

            case "prestamo":
                tasa = new BigDecimal("0.05");
                break;

            case "hipoteca":
                tasa = new BigDecimal("0.04");
                break;

            default:
                throw new CuentaInteresException(
                        "El tipo de cuenta no es válido.");
        }

        // Cálculo del interés
        BigDecimal interes = cuenta.getSaldo()
                .multiply(tasa)
                .setScale(2, RoundingMode.HALF_UP);

        cuenta.setInteresAplicado(interes);

        // Cálculo del saldo final
        BigDecimal saldoFinal = cuenta.getSaldo()
                .add(interes)
                .setScale(2, RoundingMode.HALF_UP);

        cuenta.setSaldoFinal(saldoFinal);

        return cuenta;
    }
}
