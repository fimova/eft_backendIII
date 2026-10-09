package com.duoc.clientes_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.duoc.clientes_service.dto.ClienteRequest;
import com.duoc.clientes_service.dto.ClienteResponse;
import com.duoc.clientes_service.service.ClienteService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    public ResponseEntity<List<ClienteResponse>> obtenerTodos() {
        return ResponseEntity.ok(clienteService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponse> obtenerPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(clienteService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<ClienteResponse> crear(
            @Valid @RequestBody ClienteRequest request) {

        ClienteResponse cliente = clienteService.crear(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(cliente);
    }
}
