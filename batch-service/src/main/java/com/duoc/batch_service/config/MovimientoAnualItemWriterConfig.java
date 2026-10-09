package com.duoc.batch_service.config;

import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.infrastructure.item.database.JdbcBatchItemWriter;
import org.springframework.batch.infrastructure.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.duoc.batch_service.model.MovimientoAnual;

@Configuration
public class MovimientoAnualItemWriterConfig {

    @Bean
    public ItemWriter<MovimientoAnual> movimientoAnualWriter (
        NamedParameterJdbcTemplate namedParameterJdbcTemplate) {

            return new JdbcBatchItemWriterBuilder<MovimientoAnual>()
                .namedParametersJdbcTemplate(namedParameterJdbcTemplate)
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("""
                        INSERT INTO movimientos_anuales (
                        cuenta_id,
                        fecha,
                        transaccion,
                        monto,
                        descripcion
                        )
                        VALUES (
                        :cuentaId,
                        :fecha,
                        :transaccion,
                        :monto,
                        :descripcion
                        )
                        """)
                .assertUpdates(true)
                .build();
        }
    
}
