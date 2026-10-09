package com.duoc.batch_service.config;

import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.dao.DuplicateKeyException;

import com.duoc.batch_service.exception.MovimientoAnualException;
import com.duoc.batch_service.listener.JobCompletionListener;
import com.duoc.batch_service.listener.MovimientoAnualStepExecutionListener;
import com.duoc.batch_service.model.MovimientoAnual;

//este es el batch del movimiento anual
@Configuration
public class BatchConfig {

    @Value("${app.movimiento.chunk-size}")
    private int chunkSize;

    @Value("${app.movimiento.core-pool-size}")
    private int corePoolSize;

    @Value("${app.movimiento.max-pool-size}")
    private int maxPoolSize;

    @Value("${app.movimiento.queue-capacity}")
    private int queueCapacity;
    
    @Bean
    public Step movimientoAnualStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            ItemReader<MovimientoAnual> movimientoAnualReader,
            ItemProcessor<MovimientoAnual, MovimientoAnual> movimientoAnualProcessor,
            ItemWriter<MovimientoAnual> movimientoAnualWriter,
            MovimientoAnualStepExecutionListener movimientoAnualStepExecutionListener,
            @Qualifier ("movimientoAnualTaskExecutor") ThreadPoolTaskExecutor taskExecutor
    ) {

        return new StepBuilder("movimientoAnualStep", jobRepository)
                .<MovimientoAnual, MovimientoAnual>chunk(chunkSize)
                .reader(movimientoAnualReader)
                .processor(movimientoAnualProcessor)
                .writer(movimientoAnualWriter)
                .faultTolerant() //tolerancia hacia ciertos errores
                .skip(MovimientoAnualException.class) //este explica cual tolerancia y cual saltarse
                .skip(DuplicateKeyException.class)
                .skipLimit(1000) //max skips permitidos
                .retryLimit(3)
                .retry(CannotAcquireLockException.class)
                .retry(TransientDataAccessException.class)
                .listener(movimientoAnualStepExecutionListener)
                .taskExecutor(taskExecutor)
                .build();
    }

    @Bean
    public Job movimientoAnualJob(
        JobRepository jobRepository,
        Step movimientoAnualStep,
        JobCompletionListener jobCompletionListener
    ){
        return new JobBuilder("movimientoAnualJob", jobRepository)
        .start(movimientoAnualStep)
        .listener(jobCompletionListener)
        .build();
    }

    @Bean(name="movimientoAnualTaskExecutor")
    public ThreadPoolTaskExecutor taskExecutor () {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("movimientoAnualThread");
        executor.setRejectedExecutionHandler(
            new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}
