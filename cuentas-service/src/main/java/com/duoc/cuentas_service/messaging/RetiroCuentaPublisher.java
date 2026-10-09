package com.duoc.cuentas_service.messaging;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;

import com.duoc.cuentas_service.entity.OperacionAtm;

@Component
public class RetiroCuentaPublisher {

    private static final String TOPIC = "cuentas.retiros";

    private final JmsTemplate jmsTemplate;
    private final Retry retry;
    private final CircuitBreaker circuitBreaker;

    public RetiroCuentaPublisher(
            JmsTemplate jmsTemplate,
            RetryRegistry retryRegistry,
            CircuitBreakerRegistry circuitBreakerRegistry) {

        this.jmsTemplate = jmsTemplate;
        this.retry = retryRegistry.retry("activemq");
        this.circuitBreaker = circuitBreakerRegistry.circuitBreaker("activemq");

        configurarEventos();
    }

    // publica el evento del retiro realizado
    public void publicarRetiro(RetiroCuentaEvent event) {

        System.out.println(
                "[EVENTO] Publicando retiro de cuenta " + event.getCuentaId());

        Runnable publicarConRetry = Retry.decorateRunnable(retry, () -> {

            System.out.println("[RETRY] Intento de publicación del retiro.");

            jmsTemplate.convertAndSend(TOPIC, event);
        });

        try {
            circuitBreaker.executeRunnable(publicarConRetry);

            System.out.println(
                    "[EVENTO PUBLICADO] Retiro de cuenta "
                            + event.getCuentaId()
                            + " enviado a ActiveMQ.");

        } catch (Exception e) {

            System.out.println("[ERROR PUBLICANDO EVENTO]");
            System.out.println(
                    "No fue posible publicar el retiro después de "
                            + retry.getRetryConfig().getMaxAttempts()
                            + " intentos.");

            throw e;
        }
    }

    // registra los eventos de Retry y Circuit Breaker en consola
    private void configurarEventos() {

        retry.getEventPublisher().onRetry(event -> System.out.println(
                "[RETRY] Falló intento "
                        + event.getNumberOfRetryAttempts()
                        + " de publicación."));

        circuitBreaker.getEventPublisher().onStateTransition(event -> System.out.println(
                "[CIRCUIT BREAKER] Estado: "
                        + event.getStateTransition()));
    }
}
