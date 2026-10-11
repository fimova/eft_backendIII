package com.duoc.cuentas_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.duoc.cuentas_service.entity.OperacionCuenta;

public interface OperacionCuentaRepository
        extends JpaRepository<OperacionCuenta, Long> {

    List<OperacionCuenta> findByCuentaOrigenId(Long cuentaOrigenId);

    List<OperacionCuenta> findByCuentaDestinoId(Long cuentaDestinoId);

    @Query(value = "SELECT SEQ_OPERACIONES_CUENTA.NEXTVAL FROM DUAL", nativeQuery = true)
    Long obtenerSiguienteOperacionId();
}