package com.duoc.batch_service.listener;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.listener.SkipListener;
import org.springframework.batch.core.listener.StepExecutionListener;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.stereotype.Component;

import com.duoc.batch_service.model.Transaccion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;

@Component
public class TransaccionStepExecutionListener
        implements StepExecutionListener,
                   SkipListener<Transaccion, Transaccion> {

    private long inicio;

    private static final Logger logger =
            LoggerFactory.getLogger(TransaccionStepExecutionListener.class);

    @Override
    public void beforeStep(StepExecution stepExecution) {

        inicio = System.currentTimeMillis();

        logger.info(
                "Inicio del Step. Job={}, Step={}",
                stepExecution.getJobExecution()
                        .getJobInstance()
                        .getJobName(),
                stepExecution.getStepName()
        );
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {

        long tiempoEjecucion = System.currentTimeMillis() - inicio;

        Runtime runtime = Runtime.getRuntime();

        long memoriaUsada =
                (runtime.totalMemory() - runtime.freeMemory())
                / (1024 * 1024);

        long memoriaMaxima =
                runtime.maxMemory()
                / (1024 * 1024);

        logger.info(
                "Fin del Step. Job={}, Step={}, Estado={}, Leídos={}, Escritos={}, Omitidos={}, Errores={}, Tiempo={} ms, Memoria={} MB, MemoriaMáxima={} MB",
                stepExecution.getJobExecution()
                        .getJobInstance()
                        .getJobName(),
                stepExecution.getStepName(),
                stepExecution.getStatus(),
                stepExecution.getReadCount(),
                stepExecution.getWriteCount(),
                stepExecution.getSkipCount(),
                stepExecution.getFailureExceptions().size(),
                tiempoEjecucion,
                memoriaUsada,
                memoriaMaxima
        );

        if (stepExecution.getStatus() == BatchStatus.FAILED) {

            logger.error(
                    "El Step finalizó con errores. Job={}, Step={}, Errores={}",
                    stepExecution.getJobExecution()
                            .getJobInstance()
                            .getJobName(),
                    stepExecution.getStepName(),
                    stepExecution.getFailureExceptions().size()
            );
        }

        return stepExecution.getExitStatus();
    }

    @Override
    public void onSkipInProcess(Transaccion item, Throwable t) {

        logger.warn(
                "Registro omitido durante el procesamiento. Job=transaccionJob, Step=transaccionStep, ID={}, Fecha={}, Monto={}, Tipo={}, Anomalía={}, MotivoAnomalía={}, MotivoSkip={}",
                item.getTransaccionId(),
                item.getFecha(),
                item.getMonto(),
                item.getTipo(),
                item.isAnomalia(),
                item.getMotivoAnomalia(),
                t.getMessage()
        );
        }

        @Override
        public void onSkipInRead(Throwable t) {
                
                logger.warn(
                        "Registro omitido durante la lectura. Job=transaccionJob, Step=transaccionStep, Motivo={}",
                        t.getMessage()
                );
                }
        

        @Override
        public void onSkipInWrite(Transaccion item, Throwable t) {

                logger.warn(
                        "Registro omitido durante la escritura. Job=transaccionJob, Step=transaccionStep, ID={}, Motivo={}",
                        item.getTransaccionId(),
                        t.getMessage()
                );
        }
}