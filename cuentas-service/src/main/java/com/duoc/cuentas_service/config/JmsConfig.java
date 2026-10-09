package com.duoc.cuentas_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.support.converter.JacksonJsonMessageConverter;
import org.springframework.jms.support.converter.MessageType;

@Configuration
public class JmsConfig {

    // convierte los eventos Java a mensajes JSON de texto para JMS
    @Bean
    public JacksonJsonMessageConverter messageConverter() {

        JacksonJsonMessageConverter converter = new JacksonJsonMessageConverter();

        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");

        return converter;
    }
}
