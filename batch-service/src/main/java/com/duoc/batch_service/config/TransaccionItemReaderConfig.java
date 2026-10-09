package com.duoc.batch_service.config;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.infrastructure.item.file.mapping.FieldSetMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import com.duoc.batch_service.model.Transaccion;

@Configuration
public class TransaccionItemReaderConfig {

    @Bean
    @StepScope
    public FlatFileItemReader<Transaccion> transaccion (
        @Value("${app.input.transacciones}") Resource inputFile) {

            return new FlatFileItemReaderBuilder<Transaccion>()
                .name("transaccionReader")
                .resource(inputFile)
                .encoding("UTF-8")
                .linesToSkip(1)
                .delimited()
                .delimiter(",")
                .names(
                    "transaccionId",
                    "fecha",
                    "monto",
                    "tipo"
                )
                .fieldSetMapper(transaccionFieldSetMapper())
                .build();
        }
    
    private FieldSetMapper<Transaccion> transaccionFieldSetMapper(){

        return fieldSet -> {
            
            Transaccion transaccion = new Transaccion();

            transaccion.setTransaccionId(
                fieldSet.readLong("transaccionId")
            );

            transaccion.setFecha(
                convertirFecha(
                    fieldSet.readString("fecha")
                )
            );

            transaccion.setMonto(
                fieldSet.readBigDecimal("monto")
            );

            transaccion.setTipo(
                fieldSet.readString("tipo")
            );

            return transaccion;
        };
    }

    private LocalDate convertirFecha(String valor) {

        if (valor == null || valor.isBlank()) {
            return null;
        }

        String fecha = valor.trim();

        DateTimeFormatter[] formatos = {
                DateTimeFormatter.ofPattern("dd/MM/yyyy"),
                DateTimeFormatter.ofPattern("dd-MM-yyyy"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd"),
                DateTimeFormatter.ofPattern("yyyy/MM/dd"),
                DateTimeFormatter.ofPattern("yyyy-dd-MM") //fecha extraña.. se podría dejar registro como "anomalia"
        };

        for (DateTimeFormatter formato : formatos) {
            try {
                return LocalDate.parse(fecha, formato);
            } catch (DateTimeParseException e) {
                // Intenta con el siguiente formato
            }
        }

        throw new IllegalArgumentException(
                "Formato de fecha no válido: " + fecha //también se podría intentar retornar null, que processor valide
        );
    }
}
