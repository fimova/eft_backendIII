
package com.duoc.bff_atm.dto;

import lombok.Data;
import java.math.BigDecimal;

//informacion que se expone. El saldo corresponde al SaldoFinal que haya sido procesado por el batch
@Data
public class AtmSaldoResponse {
    private Long cuentaId;
    private BigDecimal saldo;
}
