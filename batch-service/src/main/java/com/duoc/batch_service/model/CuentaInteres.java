package com.duoc.batch_service.model;

import java.math.BigDecimal;

public class CuentaInteres {

    private Long cuentaId;
    private String nombre;
    private BigDecimal saldo;
    private Integer edad;
    private String tipo;
    private BigDecimal saldoFinal;
    private BigDecimal interesAplicado;
    private String periodo;

    public CuentaInteres() {
    }

    public Long getCuentaId(){
        return cuentaId;
    }

    public void setCuentaId(Long cuentaId){
        this.cuentaId=cuentaId;
    }

    public String getNombre(){
        return nombre;
    }

    public void setNombre(String nombre){
        this.nombre=nombre;
    }

    public BigDecimal getSaldo(){
        return saldo;
    }

    public void setSaldo(BigDecimal saldo){
        this.saldo=saldo;
    }

    public Integer getEdad(){
        return edad;
    }

    public void setEdad(Integer edad){
        this.edad=edad;
    }

    public String getTipo(){
        return tipo;
    }

    public void setTipo(String tipo){
        this.tipo=tipo;
    }

    public BigDecimal getSaldoFinal() {
        return saldoFinal;
    }

    public void setSaldoFinal(BigDecimal saldoFinal){
        this.saldoFinal=saldoFinal;
    }

    public BigDecimal getInteresAplicado(){
        return interesAplicado;
    }

    public void setInteresAplicado(BigDecimal interesAplicado){
        this.interesAplicado=interesAplicado;
    }

    public String getPeriodo(){
        return periodo;
    }

    public void setPeriodo(String periodo){
        this.periodo=periodo;
    }
    
}
