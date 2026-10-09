
package com.duoc.bff_atm.client;

import com.duoc.bff_atm.dto.AtmCuentaInteresResponse;
import com.duoc.bff_atm.dto.AtmRetiroRequest;
import com.duoc.bff_atm.dto.AtmRetiroResponse;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

//usa eureka para encontrar cuentas-service y consulta el endpoint existente

@FeignClient(name = "cuentas-service")
public interface CuentasClient {

    @GetMapping("/api/cuentas/{id}/intereses")
    List<AtmCuentaInteresResponse> obtenerIntereses(
            @PathVariable("id") Long id);

    @PostMapping("/api/cuentas/{id}/retiros")
    AtmRetiroResponse realizarRetiro(
            @PathVariable("id") Long id,
            @RequestBody AtmRetiroRequest request);
}
