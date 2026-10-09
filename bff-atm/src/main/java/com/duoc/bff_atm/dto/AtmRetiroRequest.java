package com.duoc.bff_atm.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

//monto que enviará se al cajero para retirar 
@Getter
@Setter
public class AtmRetiroRequest {

    private BigDecimal monto;
}