package com.duoc.clientes_service.repository;

import com.duoc.clientes_service.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}
