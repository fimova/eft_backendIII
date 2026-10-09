package com.duoc.batch_service.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.listener.SkipListener;
import org.springframework.batch.core.listener.StepExecutionListener;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.stereotype.Component;

import com.duoc.batch_service.model.MovimientoAnual;

@Component
public class MovimientoAnualStepExecutionListener implements StepExecutionListener,
        SkipListener<MovimientoAnual, MovimientoAnual> {

    private long inicio;

    private static final Logger logger =
        LoggerFactory.getLogger(MovimientoAnualStepExecutionListener.class);

    @Override
    public void beforeStep(StepExecution stepExecution) {

        inicio = System.currentTimeMillis();

        /*
        System.out.println("======================================");
        System.out.println("INICIO DEL STEP");
        System.out.println("Step: " + stepExecution.getStepName());
        System.out.println("======================================");
         */

        logger.info("Inicio del Step. Job={}, Step={}",
        stepExecution.getJobExecution().getJobInstance().getJobName(),
        stepExecution.getStepName());
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {

        long tiempoEjecucion = System.currentTimeMillis() - inicio;

        Runtime runtime = Runtime.getRuntime();

        long memoriaUsada = (runtime.totalMemory() - runtime.freeMemory())
                / (1024 * 1024); //conversion a MB

        long memoriaMaxima = runtime.maxMemory()
                / (1024 * 1024); //conversion a MB

        /* 
        System.out.println("======================================");
        System.out.println("FIN DEL STEP");
        System.out.println("Step: " + stepExecution.getStepName());
        System.out.println("Estado: " + stepExecution.getStatus());
        System.out.println("Leídos: " + stepExecution.getReadCount());
        System.out.println("Procesados/escritos: " + stepExecution.getWriteCount());
        System.out.println("Omitidos: " + stepExecution.getSkipCount());
        System.out.println("Errores: " + stepExecution.getFailureExceptions().size());
        System.out.println("Tiempo de ejecución: " + tiempoEjecucion + " ms");
        System.out.println("Memoria utilizada: " + memoriaUsada + " MB");
        System.out.println("Memoria máxima disponible: " + memoriaMaxima + " MB");
        System.out.println("======================================");
        */

        logger.info(
            "Fin del Step. Job={}, Step={}, Estado={}, Leídos={}, Escritos={}, Omitidos={}, Errores={}, Tiempo={} ms, Memoria={} MB, MemoriaMáxima={} MB",
            stepExecution.getJobExecution().getJobInstance().getJobName(),
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
                stepExecution.getJobExecution().getJobInstance().getJobName(),
                stepExecution.getStepName(),
                stepExecution.getFailureExceptions().size()
            );
        }

        return stepExecution.getExitStatus();
    }

    @Override
    public void onSkipInProcess(MovimientoAnual item, Throwable t) {

        /* 
        System.out.println("REGISTRO OMITIDO");
        System.out.println("Cuenta: " + item.getCuentaId());
        System.out.println("Fecha: " + item.getFecha());
        System.out.println("Transacción: " + item.getTransaccion());
        System.out.println("Monto: " + item.getMonto());
        System.out.println("Descripción: " + item.getDescripcion());
        System.out.println("Motivo: " + t.getMessage());
        */

        logger.warn(
            "Registro omitido durante el procesamiento. Job=movimientoAnualJob, Step=movimientoAnualStep, Cuenta={}, Fecha={}, Transacción={}, Monto={}, Motivo={}",
            item.getCuentaId(),
            item.getFecha(),
            item.getTransaccion(),
            item.getMonto(),
            t.getMessage()
        );
    }

    @Override
    public void onSkipInRead(Throwable t) {
        /* 
        System.out.println("Registro omitido durante la lectura.");
        System.out.println("Motivo: " + t.getMessage());
        */

        logger.warn(
            "Registro omitido durante la lectura. Job=movimientoAnualJob, Step=movimientoAnualStep, Motivo={}",
            t.getMessage()
        );
    }

    @Override
    public void onSkipInWrite(MovimientoAnual item, Throwable t) {

        /*
        System.out.println("Registro omitido durante la escritura.");
        System.out.println("Cuenta: " + item.getCuentaId());
        System.out.println("Motivo: " + t.getMessage());
        */

        logger.warn(
            "Registro omitido durante la escritura. Job=movimientoAnualJob, Step=movimientoAnualStep, Cuenta={}, Motivo={}",
            item.getCuentaId(),
            t.getMessage()
        );
        
    }
}
