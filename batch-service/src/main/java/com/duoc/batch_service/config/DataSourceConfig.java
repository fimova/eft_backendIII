package com.duoc.batch_service.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class DataSourceConfig {
    
    //instanciar jdbc template para ejecutar sentencias sql utilizando jdbc sobre la bd
    @Bean
    public JdbcTemplate jdbcTemplate(DataSource datasource) {
        return new JdbcTemplate(datasource);
    }

    //crea un objeto a partir del datasource que se le entrega
    @Bean
    public NamedParameterJdbcTemplate NamedParameterJdbcTemplate (DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    //administra transacciones de bd
    @Bean
    public PlatformTransactionManager transactionManager (DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

}
