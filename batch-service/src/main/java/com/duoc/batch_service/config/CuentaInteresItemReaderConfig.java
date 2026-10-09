package com.duoc.batch_service.config;

import java.math.BigDecimal;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.infrastructure.item.file.mapping.FieldSetMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import com.duoc.batch_service.model.CuentaInteres;

@Configuration
public class CuentaInteresItemReaderConfig {

    @Bean
    @StepScope
    public FlatFileItemReader<CuentaInteres> cuentaInteresReader(
            @Value("${app.input.intereses}") Resource inputFile) {

        return new FlatFileItemReaderBuilder<CuentaInteres>()
                .name("cuentaInteresReader")
                .resource(inputFile)
                .encoding("UTF-8")
                .linesToSkip(1)
                .delimited()
                .delimiter(",")
                .names(
                        "cuentaId",
                        "nombre",
                        "saldo",
                        "edad",
                        "tipo"
                )
                .fieldSetMapper(cuentaInteresFieldSetMapper())
                .build();
    }

    private FieldSetMapper<CuentaInteres> cuentaInteresFieldSetMapper() {

                return fieldSet -> {

                        CuentaInteres cuenta = new CuentaInteres();

                        // cuentaId siempre obligatorio
                        cuenta.setCuentaId(fieldSet.readLong("cuentaId"));

                        // nombre como String directo
                        cuenta.setNombre(fieldSet.readString("nombre"));

                        // saldo: leer como string y convertir si no está vacío
                        String saldoStr = fieldSet.readString("saldo");
                        BigDecimal saldo = null;
                        if (saldoStr != null && !saldoStr.isBlank()) {
                        saldo = new BigDecimal(saldoStr);
                        }
                        cuenta.setSaldo(saldo);

                        // edad: leer como string y convertir si no está vacío
                        String edadStr = fieldSet.readString("edad");
                        Integer edad = null;
                        if (edadStr != null && !edadStr.isBlank()) {
                        edad = Integer.valueOf(edadStr);
                        }
                        cuenta.setEdad(edad);

                        // tipo como string directo
                        cuenta.setTipo(fieldSet.readString("tipo"));

                        return cuenta;
                };
        }

}
