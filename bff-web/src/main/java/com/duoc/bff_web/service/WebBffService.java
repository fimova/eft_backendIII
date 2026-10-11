package com.duoc.bff_web.service;

import com.duoc.bff_web.client.CuentasClient;
import com.duoc.bff_web.client.TransaccionesClient;
import com.duoc.bff_web.client.ClientesClient;
import com.duoc.bff_web.dto.WebClienteRequest;
import com.duoc.bff_web.dto.WebClienteResponse;
import com.duoc.bff_web.dto.WebInteresResponse;
import com.duoc.bff_web.dto.WebMovimientoResponse;
import com.duoc.bff_web.dto.WebResumenResponse;
import com.duoc.bff_web.dto.WebTransaccionResponse;
import org.springframework.stereotype.Service;

import java.util.List;

//orquestador de la información para web
@Service
public class WebBffService {

    private final CuentasClient cuentasClient;
    private final TransaccionesClient transaccionesClient;
    private final ClientesClient clientesClient;

    public WebBffService(
            CuentasClient cuentasClient,
            TransaccionesClient transaccionesClient,
            ClientesClient clientesClient) {

        this.cuentasClient = cuentasClient;
        this.transaccionesClient = transaccionesClient;
        this.clientesClient = clientesClient;
    }

    public WebResumenResponse obtenerResumen(Long cuentaId) {

        WebResumenResponse response = new WebResumenResponse();

        response.setCuenta(cuentasClient.obtenerPorId(cuentaId));

        response.setIntereses(
                cuentasClient.obtenerIntereses(cuentaId));

        response.setMovimientos(
                cuentasClient.obtenerMovimientos(cuentaId));

        response.setTransacciones(
                transaccionesClient.obtenerTodas());

        return response;
    }

    public List<WebMovimientoResponse> obtenerMovimientos(Long cuentaId) {
        return cuentasClient.obtenerMovimientos(cuentaId);
    }

    public List<WebInteresResponse> obtenerIntereses(Long cuentaId) {
        return cuentasClient.obtenerIntereses(cuentaId);
    }

    public List<WebTransaccionResponse> obtenerTransacciones() {
        return transaccionesClient.obtenerTodas();
    }

    public WebClienteResponse crearCliente(WebClienteRequest request) {
        return clientesClient.crear(request);
    }
}