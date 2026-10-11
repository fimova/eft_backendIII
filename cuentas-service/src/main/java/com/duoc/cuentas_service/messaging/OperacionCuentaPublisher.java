package com.duoc.cuentas_service.messaging;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;

@Component
public class OperacionCuentaPublisher {

    private static final String TOPIC = "cuentas.operaciones";

    private final JmsTemplate jmsTemplate;
    private final Retry retry;
    private final CircuitBreaker circuitBreaker;

    public OperacionCuentaPublisher(
            JmsTemplate jmsTemplate,
            RetryRegistry retryRegistry,
            CircuitBreakerRegistry circuitBreakerRegistry) {

        this.jmsTemplate = jmsTemplate;
        this.retry = retryRegistry.retry("activemq");
        this.circuitBreaker = circuitBreakerRegistry.circuitBreaker("activemq");
    }

    public void publicarOperacion(OperacionCuentaEvent event) {

        Runnable publicarConRetry = Retry.decorateRunnable(retry, () ->
                jmsTemplate.convertAndSend(TOPIC, event));

        circuitBreaker.executeRunnable(publicarConRetry);

        System.out.println(
                "[EVENTO PUBLICADO] Operación "
                        + event.getTipo()
                        + ", ID: "
                        + event.getOperacionId());
    }
}