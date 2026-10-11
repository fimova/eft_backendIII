
package com.duoc.cuentas_service.repository;

import com.duoc.cuentas_service.entity.OperacionAtm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OperacionAtmRepository
        extends JpaRepository<OperacionAtm, Long> {

    List<OperacionAtm> findByCuentaId(Long cuentaId);

    @Query(value = "SELECT SEQ_OPERACIONES_ATM.NEXTVAL FROM DUAL", nativeQuery = true)
    Long obtenerSiguienteOperacionId();
}
