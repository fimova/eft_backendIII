package com.duoc.bff_mobile.controller;

import com.duoc.bff_mobile.dto.MobileMovimientoResponse;
import com.duoc.bff_mobile.dto.MobileResumenResponse;
import com.duoc.bff_mobile.service.MobileBffService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mobile")
public class MobileBffController {

    private final MobileBffService mobileBffService;

    public MobileBffController(MobileBffService mobileBffService) {
        this.mobileBffService = mobileBffService;
    }

    @GetMapping("/cuentas/{cuentaId}/resumen")
    public MobileResumenResponse obtenerResumen(
            @PathVariable Long cuentaId) {

        return mobileBffService.obtenerResumen(cuentaId);
    }

    @GetMapping("/cuentas/{cuentaId}/movimientos")
    public List<MobileMovimientoResponse> obtenerMovimientos(
            @PathVariable Long cuentaId) {

        return mobileBffService.obtenerMovimientos(cuentaId);
    }
}