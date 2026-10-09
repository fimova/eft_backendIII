package com.duoc.bff_mobile.service;

import com.duoc.bff_mobile.client.CuentasClient;
import com.duoc.bff_mobile.dto.MobileInteresResponse;
import com.duoc.bff_mobile.dto.MobileMovimientoResponse;
import com.duoc.bff_mobile.dto.MobileResumenResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MobileBffService {

    private final CuentasClient cuentasClient;

    public MobileBffService(CuentasClient cuentasClient) {
        this.cuentasClient = cuentasClient;
    }

    public MobileResumenResponse obtenerResumen(Long cuentaId) {

        MobileResumenResponse response = new MobileResumenResponse();

        response.setCuenta(
                cuentasClient.obtenerPorId(cuentaId));

        List<MobileInteresResponse> intereses = cuentasClient.obtenerIntereses(cuentaId);

        if (!intereses.isEmpty()) {
            MobileInteresResponse interes = intereses.get(0);

            response.setInteresAplicado(
                    interes.getInteresAplicado());

            response.setPeriodo(
                    interes.getPeriodo());
        }

        response.setMovimientos(
                obtenerMovimientos(cuentaId));

        return response;
    }

    public List<MobileMovimientoResponse> obtenerMovimientos(
            Long cuentaId) {

        return cuentasClient.obtenerMovimientos(cuentaId)
                .stream()
                .limit(3)
                .toList();
    }
}
