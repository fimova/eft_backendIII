package com.duoc.transacciones_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.duoc.transacciones_service.entity.Transaccion;

@Repository
public interface TransaccionRepository
        extends JpaRepository<Transaccion, Long> {

    @Query(value = "SELECT SEQ_TRANSACCIONES_DIARIAS.NEXTVAL FROM DUAL", nativeQuery = true)
    Long obtenerSiguienteTransaccionId();
}