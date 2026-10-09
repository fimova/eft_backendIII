package com.duoc.bff_web.client;

import com.duoc.bff_web.dto.WebTransaccionResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "transacciones-service")
public interface TransaccionesClient {

    @GetMapping("/api/transacciones")
    List<WebTransaccionResponse> obtenerTodas();
}
