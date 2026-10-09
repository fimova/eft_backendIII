package com.duoc.cuentas_service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.duoc.cuentas_service.dto.CuentaRequest;
import com.duoc.cuentas_service.dto.CuentaResponse;
import com.duoc.cuentas_service.entity.Cuenta;
import com.duoc.cuentas_service.entity.CuentaInteres;
import com.duoc.cuentas_service.entity.MovimientoAnual;
import com.duoc.cuentas_service.exception.CuentaAlreadyExistsException;
import com.duoc.cuentas_service.exception.CuentaNotFoundException;
import com.duoc.cuentas_service.repository.CuentaInteresRepository;
import com.duoc.cuentas_service.repository.CuentaRepository;
import com.duoc.cuentas_service.repository.MovimientoAnualRepository;

import com.duoc.cuentas_service.dto.RetiroAtmRequest;
import com.duoc.cuentas_service.dto.RetiroAtmResponse;
import com.duoc.cuentas_service.entity.OperacionAtm;
import com.duoc.cuentas_service.repository.OperacionAtmRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.duoc.cuentas_service.messaging.RetiroCuentaEvent;
import com.duoc.cuentas_service.messaging.RetiroCuentaPublisher;

@Service
public class CuentaService {

    private final CuentaRepository cuentaRepository;
    private final MovimientoAnualRepository movimientoRepository;
    private final CuentaInteresRepository interesRepository;
    private final OperacionAtmRepository operacionAtmRepository;
    private final RetiroCuentaPublisher retiroCuentaPublisher;

    public CuentaService(
            CuentaRepository cuentaRepository,
            MovimientoAnualRepository movimientoRepository,
            CuentaInteresRepository interesRepository,
            OperacionAtmRepository operacionAtmRepository,
            RetiroCuentaPublisher retiroCuentaPublisher) {

        this.cuentaRepository = cuentaRepository;
        this.movimientoRepository = movimientoRepository;
        this.interesRepository = interesRepository;
        this.operacionAtmRepository = operacionAtmRepository;
        this.retiroCuentaPublisher = retiroCuentaPublisher;
    }

    public List<CuentaResponse> obtenerTodas() {

        return cuentaRepository.findAll()
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    public CuentaResponse obtenerPorId(Long id) {

        Cuenta cuenta = obtenerCuenta(id);

        return convertirAResponse(cuenta);
    }

    public CuentaResponse crear(CuentaRequest request) {

        if (cuentaRepository.existsById(request.getCuentaId())) {
            throw new CuentaAlreadyExistsException(
                    "Ya existe una cuenta con ID: " + request.getCuentaId());
        }

        Cuenta cuenta = new Cuenta();
        cuenta.setCuentaId(request.getCuentaId());
        cuenta.setEstado(request.getEstado());

        return convertirAResponse(cuentaRepository.save(cuenta));
    }

    public List<MovimientoAnual> obtenerMovimientos(Long cuentaId) {

        validarCuenta(cuentaId);

        return movimientoRepository
                .findByCuentaIdOrderByFechaDesc(cuentaId);
    }

    public List<CuentaInteres> obtenerIntereses(Long cuentaId) {

        validarCuenta(cuentaId);

        return interesRepository
                .findByCuentaIdOrderByPeriodoDesc(cuentaId);
    }

    // suponiendo que no hay retiros simultáneos sobre la misma cuenta
    @Transactional
    public RetiroAtmResponse realizarRetiro(
            Long cuentaId,
            RetiroAtmRequest request) {

        Cuenta cuenta = obtenerCuenta(cuentaId);

        if (!"ACTIVA".equalsIgnoreCase(cuenta.getEstado())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La cuenta no está activa");
        }

        BigDecimal monto = request.getMonto();

        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El monto debe ser mayor que cero");
        }

        List<CuentaInteres> intereses = interesRepository.findByCuentaIdOrderByPeriodoDesc(cuentaId);

        if (intereses.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "La cuenta no tiene información de saldo disponible");
        }

        BigDecimal saldoInicial = intereses.get(0).getSaldoFinal();

        BigDecimal retirosAnteriores = operacionAtmRepository.findByCuentaId(cuentaId)
                .stream()
                .map(OperacionAtm::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal saldoDisponible = saldoInicial.subtract(retirosAnteriores);

        if (monto.compareTo(saldoDisponible) > 0) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Saldo insuficiente para realizar el retiro");
        }

        OperacionAtm operacion = new OperacionAtm();
        operacion.setCuentaId(cuentaId);
        operacion.setTipo("RETIRO");
        operacion.setMonto(monto);
        operacion.setFecha(LocalDateTime.now());

        // guardar el retiro realizado
        operacionAtmRepository.save(operacion);

        // crear el evento que se enviará mediante JMS
        RetiroCuentaEvent evento = new RetiroCuentaEvent(
                operacion.getOperacionId(),
                cuentaId,
                monto,
                operacion.getFecha(),
                "RETIRO_ATM");

        // publicar el evento con Retry y Circuit Breaker
        retiroCuentaPublisher.publicarRetiro(evento);

        BigDecimal nuevoSaldo = saldoDisponible.subtract(monto);

        return new RetiroAtmResponse(
                cuentaId,
                monto,
                nuevoSaldo,
                "Retiro realizado correctamente");
    }

    private Cuenta obtenerCuenta(Long cuentaId) {

        return cuentaRepository.findById(cuentaId)
                .orElseThrow(() -> new CuentaNotFoundException(
                        "No existe una cuenta con ID: " + cuentaId));
    }

    private void validarCuenta(Long cuentaId) {

        if (!cuentaRepository.existsById(cuentaId)) {
            throw new CuentaNotFoundException(
                    "No existe una cuenta con ID: " + cuentaId);
        }
    }

    private CuentaResponse convertirAResponse(Cuenta cuenta) {

        return new CuentaResponse(
                cuenta.getCuentaId(),
                cuenta.getEstado());
    }
}