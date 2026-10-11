CREATE TABLE cuentas (
    cuenta_id NUMBER PRIMARY KEY,
    estado VARCHAR2(20) DEFAULT 'ACTIVA' NOT NULL
);

CREATE SEQUENCE seq_operaciones_atm
    START WITH 1
    INCREMENT BY 1
    NOCACHE;

CREATE TABLE operaciones_atm (
    operacion_id NUMBER PRIMARY KEY,
    cuenta_id NUMBER NOT NULL,
    tipo VARCHAR2(20) NOT NULL,
    monto NUMBER(15,2) NOT NULL,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,

    CONSTRAINT chk_operacion_atm_tipo
        CHECK (tipo IN ('RETIRO')),

    CONSTRAINT chk_operacion_atm_monto
        CHECK (monto > 0)
);

CREATE SEQUENCE seq_operaciones_cuenta
    START WITH 1
    INCREMENT BY 1
    NOCACHE;

CREATE TABLE operaciones_cuenta (
    operacion_id NUMBER PRIMARY KEY,
    cuenta_origen_id NUMBER,
    cuenta_destino_id NUMBER,
    tipo VARCHAR2(20) NOT NULL,
    monto NUMBER(15,2) NOT NULL,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,

    CONSTRAINT fk_op_cuenta_origen
        FOREIGN KEY (cuenta_origen_id)
        REFERENCES cuentas(cuenta_id),

    CONSTRAINT fk_op_cuenta_destino
        FOREIGN KEY (cuenta_destino_id)
        REFERENCES cuentas(cuenta_id),

    CONSTRAINT chk_op_cuenta_tipo
        CHECK (tipo IN ('DEPOSITO', 'TRANSFERENCIA')),

    CONSTRAINT chk_op_cuenta_monto
        CHECK (monto > 0),

    CONSTRAINT chk_op_cuenta_destinos
        CHECK (
            (tipo = 'DEPOSITO'
                AND cuenta_origen_id IS NULL
                AND cuenta_destino_id IS NOT NULL)
            OR
            (tipo = 'TRANSFERENCIA'
                AND cuenta_origen_id IS NOT NULL
                AND cuenta_destino_id IS NOT NULL
                AND cuenta_origen_id <> cuenta_destino_id)
        )
);