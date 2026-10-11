package com.duoc.cuentas_service.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.duoc.cuentas_service.dto.CuentaRequest;
import com.duoc.cuentas_service.dto.CuentaResponse;
import com.duoc.cuentas_service.dto.DepositoRequest;
import com.duoc.cuentas_service.entity.CuentaInteres;
import com.duoc.cuentas_service.entity.MovimientoAnual;
import com.duoc.cuentas_service.service.CuentaService;

import com.duoc.cuentas_service.dto.RetiroAtmRequest;
import com.duoc.cuentas_service.dto.RetiroAtmResponse;
import com.duoc.cuentas_service.dto.TransferenciaRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cuentas")
public class CuentaController {

        private final CuentaService cuentaService;

        public CuentaController(CuentaService cuentaService) {
                this.cuentaService = cuentaService;
        }

        @GetMapping
        public ResponseEntity<List<CuentaResponse>> obtenerTodas() {

                return ResponseEntity.ok(
                                cuentaService.obtenerTodas());
        }

        @GetMapping("/{id}")
        public ResponseEntity<CuentaResponse> obtenerPorId(
                        @PathVariable Long id) {

                return ResponseEntity.ok(
                                cuentaService.obtenerPorId(id));
        }

        @PostMapping
        public ResponseEntity<CuentaResponse> crear(
                        @Valid @RequestBody CuentaRequest request) {

                CuentaResponse cuenta = cuentaService.crear(request);

                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(cuenta);
        }

        @GetMapping("/{id}/movimientos")
        public ResponseEntity<List<MovimientoAnual>> obtenerMovimientos(
                        @PathVariable Long id) {

                return ResponseEntity.ok(
                                cuentaService.obtenerMovimientos(id));
        }

        @GetMapping("/{id}/intereses")
        public ResponseEntity<List<CuentaInteres>> obtenerIntereses(
                        @PathVariable Long id) {

                return ResponseEntity.ok(
                                cuentaService.obtenerIntereses(id));
        }

        @PostMapping("/{id}/retiros")
        public ResponseEntity<RetiroAtmResponse> realizarRetiro(
                        @PathVariable Long id,
                        @Valid @RequestBody RetiroAtmRequest request) {

                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(cuentaService.realizarRetiro(id, request));
        }

        @PostMapping("/{id}/depositos")
        public ResponseEntity<BigDecimal> realizarDeposito(
                        @PathVariable Long id,
                        @Valid @RequestBody DepositoRequest request) {

                BigDecimal nuevoSaldo = cuentaService.realizarDeposito(id, request);

                return ResponseEntity.ok(nuevoSaldo);
        }

        @PostMapping("/{id}/transferencias")
        public ResponseEntity<BigDecimal> realizarTransferencia(
                        @PathVariable Long id,
                        @Valid @RequestBody TransferenciaRequest request) {

                BigDecimal nuevoSaldo = cuentaService.realizarTransferencia(id, request);

                return ResponseEntity.ok(nuevoSaldo);
        }

}