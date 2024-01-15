package com.ladera.batchprocessing.config.dealer;

import com.ladera.batchprocessing.config.BaseBatchConfig;
import com.ladera.batchprocessing.entity.Dealer;
import com.ladera.batchprocessing.repository.DealerRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class DealerBatchConfig extends BaseBatchConfig<Dealer,Dealer> {

    @Autowired
    private DealerRepository dealerRepository;

    @Override
    @Bean(name = "dealerReader")
    public ItemReader<Dealer> createItemReader() {
        FlatFileItemReader<Dealer> itemReader = new FlatFileItemReader<>();
        itemReader.setResource(new FileSystemResource("src/main/resources/dealers/dealers.csv"));
        itemReader.setName("csvReader");
        itemReader.setLinesToSkip(1);
        itemReader.setLineMapper(linemapper());
        return itemReader;
    }

    private LineMapper<Dealer> linemapper() {
        DefaultLineMapper<Dealer> lineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("id", "firstName", "lastName", "email", "country", "dob");
        BeanWrapperFieldSetMapper<Dealer> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Dealer.class);

        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);

        return lineMapper;
    }

    @Override
    @Bean(name = "dealerProcessor")
    public ItemProcessor<Dealer, Dealer> createItemProcessor() {
        return new DealerProcessor();
    }

    @Override
    @Bean(name = "dealerWriter")
    public ItemWriter<Dealer> createItemWriter() {
        RepositoryItemWriter<Dealer> writer= new RepositoryItemWriter<>();
        writer.setRepository(dealerRepository);
        writer.setMethodName("save");
        return writer;
    }
    @Override
    @Bean(name = "dealerStep")
    public Step step(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("csv-step",jobRepository)
                .<Dealer,Dealer>chunk(10,transactionManager)
                .reader(createItemReader())
                .processor(createItemProcessor())
                .writer(createItemWriter())
                .build();    }

    @Override
    @Bean(name = "dealerRunJob")
    public Job runJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new JobBuilder("importDealers",jobRepository)
                .flow(step(jobRepository,transactionManager))
                .end().build();
    }
}