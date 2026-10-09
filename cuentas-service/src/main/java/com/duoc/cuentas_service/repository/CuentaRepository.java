package com.duoc.cuentas_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.duoc.cuentas_service.entity.Cuenta;

public interface CuentaRepository extends JpaRepository<Cuenta, Long> {
}