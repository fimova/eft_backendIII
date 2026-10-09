package com.duoc.clientes_service.messaging;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;

@Component
public class ClienteCreadoPublisher {

    private static final String TOPIC = "clientes.creados";

    private final JmsTemplate jmsTemplate;
    private final Retry retry;
    private final CircuitBreaker circuitBreaker;

    public ClienteCreadoPublisher(
            JmsTemplate jmsTemplate,
            RetryRegistry retryRegistry,
            CircuitBreakerRegistry circuitBreakerRegistry) {

        this.jmsTemplate = jmsTemplate;
        this.retry = retryRegistry.retry("activemq");
        this.circuitBreaker = circuitBreakerRegistry.circuitBreaker("activemq");

        configurarEventos();
    }

    public void publicarClienteCreado(ClienteCreadoEvent evento) {

        System.out.println(
                "[EVENTO] Publicando creación del cliente " + evento.getClienteId());

        Runnable publicarConRetry = Retry.decorateRunnable(
                retry,
                () -> {
                    System.out.println("[RETRY] Intento de publicación del cliente.");
                    jmsTemplate.convertAndSend(TOPIC, evento);
                });

        try {
            circuitBreaker.executeRunnable(publicarConRetry);

            System.out.println(
                    "[EVENTO PUBLICADO] Cliente "
                            + evento.getClienteId()
                            + " enviado a ActiveMQ.");

        } catch (Exception e) {
            System.out.println("[ERROR PUBLICANDO EVENTO]");
            System.out.println(
                    "No fue posible publicar el cliente después de "
                            + retry.getRetryConfig().getMaxAttempts()
                            + " intentos.");

            throw e;
        }
    }

    private void configurarEventos() {

        retry.getEventPublisher().onRetry(event ->
                System.out.println(
                        "[RETRY] Falló intento "
                                + event.getNumberOfRetryAttempts()
                                + " de publicación."));

        circuitBreaker.getEventPublisher().onStateTransition(event ->
                System.out.println(
                        "[CIRCUIT BREAKER] Estado: "
                                + event.getStateTransition()));
    }
}
