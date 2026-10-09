package com.duoc.mensajeria_service.messaging;

import com.duoc.mensajeria_service.entity.EventoProcesado;
import com.duoc.mensajeria_service.repository.EventoProcesadoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

//escucha eventos de transacciones service
@Component
public class TransaccionCreadaListener {

        private final ObjectMapper objectMapper;
        private final EventoProcesadoRepository eventoProcesadoRepository;

        public TransaccionCreadaListener(
                        ObjectMapper objectMapper,
                        EventoProcesadoRepository eventoProcesadoRepository) {

                this.objectMapper = objectMapper;
                this.eventoProcesadoRepository = eventoProcesadoRepository;
        }

        @JmsListener(destination = "transacciones.creadas")
        public void recibirTransaccion(String mensaje) throws Exception {

                TransaccionCreadaEvent event = objectMapper.readValue(
                                mensaje,
                                TransaccionCreadaEvent.class);

                Long transaccionId = event.getTransaccionId();
                String servicioOrigen = "transacciones-service";
                String tipoEvento = "TRANSACCION_CREADA";

                System.out.println(
                                "[EVENTO RECIBIDO] Transacción: " + transaccionId);

                // Verificar si la transacción ya fue procesada correctamente.
                boolean yaProcesado = eventoProcesadoRepository
                                .existsByServicioOrigenAndTipoEventoAndReferenciaIdAndEstado(
                                                servicioOrigen,
                                                tipoEvento,
                                                transaccionId,
                                                "PROCESADO");

                if (yaProcesado) {
                        System.out.println(
                                        "[EVENTO DUPLICADO] Transacción " + transaccionId
                                                        + " ya fue procesada. Se ignora redelivery.");
                        return;
                }

                System.out.println("[PROCESAMIENTO] Procesando transacción...");

                try {
                        // error intencional para comprobar el redelivery de JMS
                        if (transaccionId.equals(99999L)) {
                                throw new RuntimeException(
                                                "Error intencional para prueba de procesamiento");
                        }

                        EventoProcesado registro = new EventoProcesado();

                        registro.setTransaccionId(transaccionId);
                        registro.setReferenciaId(transaccionId);
                        registro.setServicioOrigen(servicioOrigen);
                        registro.setTipoEvento(tipoEvento);
                        registro.setFechaProcesamiento(LocalDateTime.now());
                        registro.setEstado("PROCESADO");
                        registro.setDetalle("Transacción procesada correctamente");

                        eventoProcesadoRepository.save(registro);

                        System.out.println(
                                        "[PROCESAMIENTO EXITOSO] Transacción " + transaccionId
                                                        + " procesada correctamente.");

                } catch (Exception e) {

                        EventoProcesado registroError = new EventoProcesado();

                        registroError.setTransaccionId(transaccionId);
                        registroError.setServicioOrigen(servicioOrigen);
                        registroError.setTipoEvento(tipoEvento);
                        registroError.setFechaProcesamiento(LocalDateTime.now());
                        registroError.setEstado("ERROR");
                        registroError.setDetalle(
                                        "Error al procesar transacción: " + e.getMessage());

                        eventoProcesadoRepository.save(registroError);

                        System.out.println(
                                        "[ERROR DE PROCESAMIENTO] Transacción " + transaccionId
                                                        + " no pudo ser procesada.");
                        System.out.println("Motivo: " + e.getMessage());

                        // relanzar el error para permitir que JMS intente redelivery
                        throw e;
                }
        }
}