package com.duoc.mensajeria_service.messaging;

import java.time.LocalDateTime;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.duoc.mensajeria_service.entity.EventoProcesado;
import com.duoc.mensajeria_service.repository.EventoProcesadoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class RetiroCuentaListener {

    private final EventoProcesadoRepository eventoProcesadoRepository;
    private final ObjectMapper objectMapper;

    public RetiroCuentaListener(
            EventoProcesadoRepository eventoProcesadoRepository,
            ObjectMapper objectMapper) {
        this.eventoProcesadoRepository = eventoProcesadoRepository;
        this.objectMapper = objectMapper;
    }

    @JmsListener(destination = "cuentas.retiros")
    public void recibirRetiro(String mensaje) throws Exception {

        RetiroCuentaEvent evento = objectMapper.readValue(mensaje, RetiroCuentaEvent.class);

        String servicioOrigen = "cuentas-service";
        String tipoEvento = evento.getTipo();

        System.out.println(
                "[EVENTO RECIBIDO] Retiro de cuenta: "
                        + evento.getCuentaId()
                        + ", operación: "
                        + evento.getOperacionId());

        // verificar si el evento ya fue procesado
        boolean yaProcesado = eventoProcesadoRepository
                .existsByServicioOrigenAndTipoEventoAndReferenciaIdAndEstado(
                        servicioOrigen,
                        tipoEvento,
                        evento.getOperacionId(),
                        "PROCESADO");

        if (yaProcesado) {
            System.out.println(
                    "[EVENTO DUPLICADO] La operación "
                            + evento.getOperacionId()
                            + " ya fue procesada. Se ignora el mensaje.");
            return;
        }

        // registrar el evento recibido en Oracle
        EventoProcesado registro = new EventoProcesado();
        registro.setTransaccionId(null);
        registro.setReferenciaId(evento.getOperacionId());
        registro.setServicioOrigen(servicioOrigen);
        registro.setTipoEvento(tipoEvento);
        registro.setFechaProcesamiento(LocalDateTime.now());
        registro.setEstado("PROCESADO");
        registro.setDetalle(
                "Retiro ATM de cuenta "
                        + evento.getCuentaId()
                        + " por monto "
                        + evento.getMonto());

        eventoProcesadoRepository.save(registro);

        System.out.println(
                "[PROCESAMIENTO EXITOSO] Operación "
                        + evento.getOperacionId()
                        + " registrada en eventos_procesados.");
    }
}