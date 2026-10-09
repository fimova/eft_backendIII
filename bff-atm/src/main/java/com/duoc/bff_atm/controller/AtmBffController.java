
package com.duoc.bff_atm.controller;

import com.duoc.bff_atm.dto.AtmSaldoResponse;
import com.duoc.bff_atm.service.AtmBffService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.duoc.bff_atm.dto.AtmRetiroRequest;
import com.duoc.bff_atm.dto.AtmRetiroResponse;

@RestController
@RequestMapping("/api/atm")
public class AtmBffController {

    private final AtmBffService atmBffService;

    public AtmBffController(AtmBffService atmBffService) {
        this.atmBffService = atmBffService;
    }

    @GetMapping("/cuentas/{cuentaId}/saldo")
    public ResponseEntity<AtmSaldoResponse> obtenerSaldo(
            @PathVariable Long cuentaId) {

        return ResponseEntity.ok(
                atmBffService.obtenerSaldo(cuentaId));
    }

    @PostMapping("/cuentas/{cuentaId}/retiros")
    public ResponseEntity<AtmRetiroResponse> realizarRetiro(
            @PathVariable Long cuentaId,
            @RequestBody AtmRetiroRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(atmBffService.realizarRetiro(cuentaId, request));
    }
}
