package com.duoc.bff_web.client;

import com.duoc.bff_web.dto.WebCuentaResponse;
import com.duoc.bff_web.dto.WebInteresResponse;
import com.duoc.bff_web.dto.WebMovimientoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "cuentas-service")
public interface CuentasClient {

    @GetMapping("/api/cuentas")
    List<WebCuentaResponse> obtenerTodas();

    @GetMapping("/api/cuentas/{id}")
    WebCuentaResponse obtenerPorId(@PathVariable("id") Long id);

    @GetMapping("/api/cuentas/{id}/movimientos")
    List<WebMovimientoResponse> obtenerMovimientos(
            @PathVariable("id") Long id);

    @GetMapping("/api/cuentas/{id}/intereses")
    List<WebInteresResponse> obtenerIntereses(
            @PathVariable("id") Long id);
}