package com.duoc.clientes_service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.duoc.clientes_service.dto.ClienteRequest;
import com.duoc.clientes_service.dto.ClienteResponse;
import com.duoc.clientes_service.entity.Cliente;
import com.duoc.clientes_service.exception.ClienteAlreadyExistsException;
import com.duoc.clientes_service.exception.ClienteNotFoundException;
import com.duoc.clientes_service.repository.ClienteRepository;

import com.duoc.clientes_service.messaging.ClienteCreadoEvent;
import com.duoc.clientes_service.messaging.ClienteCreadoPublisher;
import java.time.LocalDateTime;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteCreadoPublisher clienteCreadoPublisher;

    public ClienteService(ClienteRepository clienteRepository,
            ClienteCreadoPublisher clienteCreadoPublisher) {
        this.clienteRepository = clienteRepository;
        this.clienteCreadoPublisher = clienteCreadoPublisher;
    }

    public List<ClienteResponse> obtenerTodos() {
        return clienteRepository.findAll()
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    public ClienteResponse obtenerPorId(Long id) {
        Cliente cliente = obtenerCliente(id);
        return convertirAResponse(cliente);
    }

    public ClienteResponse crear(ClienteRequest request) {

        if (clienteRepository.existsById(request.getClienteId())) {
            throw new ClienteAlreadyExistsException(
                    "Ya existe un cliente con ID: " + request.getClienteId());
        }

        Cliente cliente = new Cliente();
        cliente.setClienteId(request.getClienteId());
        cliente.setNombre(request.getNombre());
        cliente.setEmail(request.getEmail());
        cliente.setEstado(request.getEstado());

        Cliente clienteGuardado = clienteRepository.save(cliente);

        ClienteCreadoEvent evento = new ClienteCreadoEvent(
                clienteGuardado.getClienteId(),
                LocalDateTime.now(),
                "CLIENTE_CREADO");

        clienteCreadoPublisher.publicarClienteCreado(evento);

        return convertirAResponse(clienteGuardado);
    }

    private Cliente obtenerCliente(Long clienteId) {
        return clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ClienteNotFoundException(
                        "No existe un cliente con ID: " + clienteId));
    }

    private ClienteResponse convertirAResponse(Cliente cliente) {
        return new ClienteResponse(
                cliente.getClienteId(),
                cliente.getNombre(),
                cliente.getEmail(),
                cliente.getEstado());
    }
}