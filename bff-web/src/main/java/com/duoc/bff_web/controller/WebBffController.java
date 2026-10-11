package com.duoc.bff_web.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.duoc.bff_web.dto.WebClienteRequest;
import com.duoc.bff_web.dto.WebClienteResponse;
import com.duoc.bff_web.dto.WebInteresResponse;
import com.duoc.bff_web.dto.WebMovimientoResponse;
import com.duoc.bff_web.dto.WebResumenResponse;
import com.duoc.bff_web.dto.WebTransaccionResponse;

import com.duoc.bff_web.service.WebBffService;

@RestController
@RequestMapping("/api/web")
public class WebBffController {

    private final WebBffService webBffService;

    public WebBffController(WebBffService webBffService) {
        this.webBffService = webBffService;
    }

    @GetMapping("/cuentas/{cuentaId}/resumen")
    public WebResumenResponse obtenerResumen(
            @PathVariable Long cuentaId) {
        return webBffService.obtenerResumen(cuentaId);
    }

    @GetMapping("/cuentas/{cuentaId}/movimientos")
    public List<WebMovimientoResponse> obtenerMovimientos(
            @PathVariable Long cuentaId) {
        return webBffService.obtenerMovimientos(cuentaId);
    }

    @GetMapping("/cuentas/{cuentaId}/intereses")
    public List<WebInteresResponse> obtenerIntereses(
            @PathVariable Long cuentaId) {
        return webBffService.obtenerIntereses(cuentaId);
    }

    @GetMapping("/transacciones")
    public List<WebTransaccionResponse> obtenerTransacciones() {
        return webBffService.obtenerTransacciones();
    }

    @PostMapping("/clientes")
    public ResponseEntity<WebClienteResponse> crearCliente(
            @RequestBody WebClienteRequest request) {

        WebClienteResponse cliente = webBffService.crearCliente(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(cliente);
    }
}