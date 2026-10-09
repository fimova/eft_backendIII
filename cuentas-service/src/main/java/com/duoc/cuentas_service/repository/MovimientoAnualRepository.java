package com.duoc.cuentas_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.duoc.cuentas_service.entity.MovimientoAnual;

public interface MovimientoAnualRepository
        extends JpaRepository<MovimientoAnual, Long> {

    List<MovimientoAnual> findByCuentaIdOrderByFechaDesc(Long cuentaId);
}