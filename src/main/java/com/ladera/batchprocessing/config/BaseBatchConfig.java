package com.ladera.batchprocessing.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public abstract class BaseBatchConfig<T, S> {

    public abstract ItemReader<T> createItemReader();

    public abstract ItemProcessor<T, S> createItemProcessor();

    public abstract ItemWriter<S> createItemWriter();

    public abstract Step step(JobRepository jobRepository, PlatformTransactionManager transactionManager);

    public abstract Job runJob(JobRepository jobRepository, PlatformTransactionManager transactionManager);

}