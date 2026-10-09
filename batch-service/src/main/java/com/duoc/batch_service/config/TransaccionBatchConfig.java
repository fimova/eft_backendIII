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
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.duoc.batch_service.exception.TransaccionException;
import com.duoc.batch_service.listener.JobCompletionListener;
import com.duoc.batch_service.listener.TransaccionStepExecutionListener;
import com.duoc.batch_service.model.Transaccion;

@Configuration
public class TransaccionBatchConfig {

    @Value("${app.transaccion.chunk-size}")
    private int chunkSize;

    @Value("${app.transaccion.core-pool-size}")
    private int corePoolSize;

    @Value("${app.transaccion.max-pool-size}")
    private int maxPoolSize;

    @Value("${app.transaccion.queue-capacity}")
    private int queueCapacity;

    @Bean
    public Step transaccionStep(
            JobRepository jobRepository,
            ItemReader<Transaccion> transaccionReader,
            ItemProcessor<Transaccion, Transaccion> transaccionProcessor,
            ItemWriter<Transaccion> transaccionWriter,
            TransaccionStepExecutionListener transaccionStepExecutionListener,
            @Qualifier ("transaccionTaskExecutor") ThreadPoolTaskExecutor taskExecutor
    ) {

        return new StepBuilder("transaccionStep", jobRepository)
                .<Transaccion, Transaccion>chunk(chunkSize)
                .reader(transaccionReader)
                .processor(transaccionProcessor)
                .writer(transaccionWriter)
                .faultTolerant()
                .skipLimit(1000)
                .skip(TransaccionException.class)
                .skip(DuplicateKeyException.class)
                .retryLimit(3)
                .retry(CannotAcquireLockException.class)
                .retry(TransientDataAccessException.class)
                .listener(transaccionStepExecutionListener)
                .taskExecutor(taskExecutor)
                .build();
    }

    @Bean
    public Job transaccionJob(
            JobRepository jobRepository,
            Step transaccionStep,
            JobCompletionListener jobCompletionListener
    ) {
        return new JobBuilder("transaccionJob", jobRepository)
                .start(transaccionStep)
                .listener(jobCompletionListener)
                .build();
    }

    @Bean(name="transaccionTaskExecutor")
    public ThreadPoolTaskExecutor taskExecutor () {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);;
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("transaccionThread");
        executor.setRejectedExecutionHandler(
            new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
    
}
