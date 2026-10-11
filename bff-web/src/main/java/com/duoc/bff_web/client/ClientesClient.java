package com.duoc.bff_web.client;

import com.duoc.bff_web.dto.WebClienteRequest;
import com.duoc.bff_web.dto.WebClienteResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

//encuentra el MS mediante eureka y llama al endpoint que crea clientes
@FeignClient(name = "clientes-service")
public interface ClientesClient {

    @PostMapping("/api/clientes")
    WebClienteResponse crear(
            @RequestBody WebClienteRequest request);
}