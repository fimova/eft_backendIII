package com.duoc.batch_service.config;

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
import org.springframework.transaction.PlatformTransactionManager;

import com.duoc.batch_service.exception.CuentaInteresException;
import com.duoc.batch_service.listener.CuentaInteresStepExecutionListener;
import com.duoc.batch_service.listener.JobCompletionListener;
import com.duoc.batch_service.model.CuentaInteres;

@Configuration
public class CuentaInteresBatchConfig {

    @Value("${app.interes.chunk-size}")
    private int chunkSize;

    @Value("${app.interes.core-pool-size}")
    private int corePoolSize;

    @Value("${app.interes.max-pool-size}")
    private int maxPoolSize;

    @Value("${app.interes.queue-capacity}")
    private int queueCapacity;

    @Bean
    public Step cuentaInteresStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            ItemReader<CuentaInteres> cuentaInteresReader,
            ItemProcessor<CuentaInteres, CuentaInteres> cuentaInteresProcessor,
            ItemWriter<CuentaInteres> cuentaInteresWriter,
            CuentaInteresStepExecutionListener cuentaInteresStepExecutionListener,
            @Qualifier ("cuentaInteresTaskExecutor") ThreadPoolTaskExecutor taskExecutor
    ) {

        return new StepBuilder("cuentaInteresStep", jobRepository)
                .<CuentaInteres, CuentaInteres>chunk(chunkSize)
                .reader(cuentaInteresReader)
                .processor(cuentaInteresProcessor)
                .writer(cuentaInteresWriter)
                .faultTolerant()
                .skip(CuentaInteresException.class)
                .skip(DuplicateKeyException.class)
                .skipLimit(1000)
                .retryLimit(3)
                .retry(CannotAcquireLockException.class)
                .retry(TransientDataAccessException.class)
                .listener(cuentaInteresStepExecutionListener)
                .transactionManager(transactionManager)
                .taskExecutor(taskExecutor)
                .build();
    }

    @Bean
    public Job cuentaInteresJob(
            JobRepository jobRepository,
            Step cuentaInteresStep,
            JobCompletionListener jobCompletionListener
    ) {

        return new JobBuilder("cuentaInteresJob", jobRepository)
                .start(cuentaInteresStep)
                .listener(jobCompletionListener)
                .build();
    }

    @Bean(name="cuentaInteresTaskExecutor")
    public ThreadPoolTaskExecutor taskExecutor () {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("cuentaInteresThread");
        executor.initialize();
        return executor;
    }
}