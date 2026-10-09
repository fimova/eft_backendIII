package com.duoc.batch_service.config;

import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.database.ItemSqlParameterSourceProvider;
import org.springframework.batch.infrastructure.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.duoc.batch_service.model.Transaccion;

@Configuration
public class TransaccionItemWriterConfig {

    @Bean
    public ItemWriter<Transaccion> transaccionWriter(
            NamedParameterJdbcTemplate namedParameterJdbcTemplate) {

        return new JdbcBatchItemWriterBuilder<Transaccion>()
                .namedParametersJdbcTemplate(namedParameterJdbcTemplate)
                .itemSqlParameterSourceProvider(transaccionParameterSourceProvider())
                .sql("""
                        INSERT INTO transacciones_diarias (
                            transaccion_id,
                            fecha,
                            monto,
                            tipo,
                            anomalia,
                            motivo_anomalia
                        )
                        VALUES (
                            :transaccionId,
                            :fecha,
                            :monto,
                            :tipo,
                            :anomalia,
                            :motivoAnomalia
                        )
                        """)
                .assertUpdates(true)
                .build();
    }

    private ItemSqlParameterSourceProvider<Transaccion>
            transaccionParameterSourceProvider() {

        return transaccion -> {

            MapSqlParameterSource parameters =
                    new MapSqlParameterSource();

            parameters.addValue(
                    "transaccionId",
                    transaccion.getTransaccionId()
            );

            parameters.addValue(
                    "fecha",
                    transaccion.getFecha()
            );

            parameters.addValue(
                    "monto",
                    transaccion.getMonto()
            );

            parameters.addValue(
                    "tipo",
                    transaccion.getTipo()
            );

            parameters.addValue(
                    "anomalia",
                    transaccion.isAnomalia() ? 1 : 0
            );

            parameters.addValue(
                    "motivoAnomalia",
                    transaccion.getMotivoAnomalia()
            );

            return parameters;
        };
    }
}