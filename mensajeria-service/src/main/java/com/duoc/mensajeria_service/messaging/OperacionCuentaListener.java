package com.duoc.mensajeria_service.messaging;

import java.time.LocalDateTime;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.duoc.mensajeria_service.entity.EventoProcesado;
import com.duoc.mensajeria_service.repository.EventoProcesadoRepository;

import tools.jackson.databind.ObjectMapper;

//registra que el evento fue procesado; no ejecuta el depósito ni la transferencia
@Component
public class OperacionCuentaListener {

    private final EventoProcesadoRepository eventoProcesadoRepository;
    private final ObjectMapper objectMapper;

    public OperacionCuentaListener(
            EventoProcesadoRepository eventoProcesadoRepository,
            ObjectMapper objectMapper) {
        this.eventoProcesadoRepository = eventoProcesadoRepository;
        this.objectMapper = objectMapper;
    }

    @JmsListener(destination = "cuentas.operaciones")
    public void recibirOperacion(String mensaje) throws Exception {

        OperacionCuentaEvent evento = objectMapper.readValue(mensaje, OperacionCuentaEvent.class);

        String servicioOrigen = "cuentas-service";
        String tipoEvento = evento.getTipo();

        System.out.println(
                "[EVENTO RECIBIDO] "
                        + tipoEvento
                        + ", operación: "
                        + evento.getOperacionId());

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

        EventoProcesado registro = new EventoProcesado();
        registro.setTransaccionId(null);
        registro.setReferenciaId(evento.getOperacionId());
        registro.setServicioOrigen(servicioOrigen);
        registro.setTipoEvento(tipoEvento);
        registro.setFechaProcesamiento(LocalDateTime.now());
        registro.setEstado("PROCESADO");
        registro.setDetalle(
                tipoEvento
                        + " de cuenta origen "
                        + evento.getCuentaOrigenId()
                        + " a cuenta destino "
                        + evento.getCuentaDestinoId()
                        + ", monto "
                        + evento.getMonto());

        eventoProcesadoRepository.save(registro);

        System.out.println(
                "[PROCESAMIENTO EXITOSO] Operación "
                        + evento.getOperacionId()
                        + " registrada en eventos_procesados.");
    }
}