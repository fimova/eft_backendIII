package com.duoc.cuentas_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.duoc.cuentas_service.entity.CuentaInteres;

public interface CuentaInteresRepository
        extends JpaRepository<CuentaInteres, Long> {

    List<CuentaInteres> findByCuentaIdOrderByPeriodoDesc(Long cuentaId);
}