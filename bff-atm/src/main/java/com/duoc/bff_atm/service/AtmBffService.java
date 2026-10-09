
package com.duoc.bff_atm.service;

import com.duoc.bff_atm.client.CuentasClient;
import com.duoc.bff_atm.dto.AtmCuentaInteresResponse;
import com.duoc.bff_atm.dto.AtmSaldoResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import com.duoc.bff_atm.dto.AtmRetiroRequest;
import com.duoc.bff_atm.dto.AtmRetiroResponse;

//consulta intereses de la cuenta mediante cuentasclient
//toma el primer registro del periodo mas reciente porque el service ordena en periodo DESC
//devuelve saldo final
//si no existen registros, da error HTTP 404
//delega la validación del saldo retirado y el MS (cuentas-service) lo persiste
@Service
public class AtmBffService {

    private final CuentasClient cuentasClient;

    public AtmBffService(CuentasClient cuentasClient) {
        this.cuentasClient = cuentasClient;
    }

    public AtmSaldoResponse obtenerSaldo(Long cuentaId) {
        List<AtmCuentaInteresResponse> intereses = cuentasClient.obtenerIntereses(cuentaId);

        if (intereses == null || intereses.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "La cuenta no tiene información de saldo disponible");
        }

        AtmCuentaInteresResponse ultimoPeriodo = intereses.get(0);

        AtmSaldoResponse response = new AtmSaldoResponse();
        response.setCuentaId(cuentaId);
        response.setSaldo(ultimoPeriodo.getSaldoFinal());

        return response;
    }

    public AtmRetiroResponse realizarRetiro(
            Long cuentaId,
            AtmRetiroRequest request) {

        return cuentasClient.realizarRetiro(cuentaId, request);
    }
}
