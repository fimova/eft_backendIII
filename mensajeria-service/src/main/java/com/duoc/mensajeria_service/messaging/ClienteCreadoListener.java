package com.duoc.mensajeria_service.messaging;

import java.time.LocalDateTime;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.duoc.mensajeria_service.entity.EventoProcesado;
import com.duoc.mensajeria_service.repository.EventoProcesadoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ClienteCreadoListener {

    private final EventoProcesadoRepository eventoProcesadoRepository;
    private final ObjectMapper objectMapper;

    public ClienteCreadoListener(
            EventoProcesadoRepository eventoProcesadoRepository,
            ObjectMapper objectMapper) {
        this.eventoProcesadoRepository = eventoProcesadoRepository;
        this.objectMapper = objectMapper;
    }

    @JmsListener(destination = "clientes.creados")
    public void recibirClienteCreado(String mensaje) throws Exception {

        ClienteCreadoEvent evento = objectMapper.readValue(mensaje, ClienteCreadoEvent.class);

        String servicioOrigen = "clientes-service";
        String tipoEvento = evento.getTipo();

        System.out.println(
                "[EVENTO RECIBIDO] Creación del cliente: "
                        + evento.getClienteId());

        boolean yaProcesado = eventoProcesadoRepository
                .existsByServicioOrigenAndTipoEventoAndReferenciaIdAndEstado(
                        servicioOrigen,
                        tipoEvento,
                        evento.getClienteId(),
                        "PROCESADO");

        if (yaProcesado) {
            System.out.println(
                    "[EVENTO DUPLICADO] La creación del cliente "
                            + evento.getClienteId()
                            + " ya fue procesada. Se ignora el mensaje.");
            return;
        }

        EventoProcesado registro = new EventoProcesado();
        registro.setTransaccionId(null);
        registro.setReferenciaId(evento.getClienteId());
        registro.setServicioOrigen(servicioOrigen);
        registro.setTipoEvento(tipoEvento);
        registro.setFechaProcesamiento(LocalDateTime.now());
        registro.setEstado("PROCESADO");
        registro.setDetalle(
                "Cliente creado con ID " + evento.getClienteId());

        eventoProcesadoRepository.save(registro);

        System.out.println(
                "[PROCESAMIENTO EXITOSO] Creación del cliente "
                        + evento.getClienteId()
                        + " registrada en eventos_procesados.");
    }
}
