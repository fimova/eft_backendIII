package com.duoc.batch_service.config;

import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.infrastructure.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.duoc.batch_service.model.CuentaInteres;

@Configuration
public class CuentaInteresItemWriterConfig {

    @Bean
    public ItemWriter<CuentaInteres> cuentaInteresWriter(
            NamedParameterJdbcTemplate namedParameterJdbcTemplate) {

        return new JdbcBatchItemWriterBuilder<CuentaInteres>()
                .namedParametersJdbcTemplate(namedParameterJdbcTemplate)
                .itemSqlParameterSourceProvider(
                        new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("""
                        INSERT INTO cuentas_intereses (
                            cuenta_id,
                            nombre,
                            saldo_inicial,
                            edad,
                            tipo,
                            interes_aplicado,
                            saldo_final,
                            periodo
                        )
                        VALUES (
                            :cuentaId,
                            :nombre,
                            :saldo,
                            :edad,
                            :tipo,
                            :interesAplicado,
                            :saldoFinal,
                            :periodo
                        )
                        """)
                .assertUpdates(true)
                .build();
    }
}
