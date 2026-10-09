package com.duoc.bff_mobile.client;

import com.duoc.bff_mobile.dto.MobileCuentaResponse;
import com.duoc.bff_mobile.dto.MobileMovimientoResponse;
import com.duoc.bff_mobile.dto.MobileInteresResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "cuentas-service")
public interface CuentasClient {

    @GetMapping("/api/cuentas/{id}")
    MobileCuentaResponse obtenerPorId(
            @PathVariable("id") Long id);

    @GetMapping("/api/cuentas/{id}/movimientos")
    List<MobileMovimientoResponse> obtenerMovimientos(
            @PathVariable("id") Long id);

    @GetMapping("/api/cuentas/{id}/intereses")
    List<MobileInteresResponse> obtenerIntereses(
            @PathVariable("id") Long id);
}
