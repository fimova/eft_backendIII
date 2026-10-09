package com.duoc.mensajeria_service.repository;

import com.duoc.mensajeria_service.entity.EventoProcesado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//se readapta para recibir eventos de los 3 MS de negocio ahora
//verificar un evento usando su servicio de origen, tipo, ID de referencia y estado
@Repository
public interface EventoProcesadoRepository
                extends JpaRepository<EventoProcesado, Long> {

        // verifica si una transacción ya fue procesada correctamente
        boolean existsByTransaccionIdAndTipoEventoAndEstado(
                        Long transaccionId,
                        String tipoEvento,
                        String estado);

        // verifica si un evento de cualquier microservicio ya fue procesado.
        boolean existsByServicioOrigenAndTipoEventoAndEstado(
                        String servicioOrigen,
                        String tipoEvento,
                        String estado);

        boolean existsByServicioOrigenAndTipoEventoAndReferenciaIdAndEstado(
                        String servicioOrigen,
                        String tipoEvento,
                        Long referenciaId,
                        String estado);
}
