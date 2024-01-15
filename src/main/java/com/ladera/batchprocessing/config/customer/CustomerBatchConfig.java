package com.ladera.batchprocessing.config.customer;

import com.ladera.batchprocessing.config.BaseBatchConfig;
import com.ladera.batchprocessing.entity.Customer;
import com.ladera.batchprocessing.repository.CustomerRepository;
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
public class CustomerBatchConfig extends BaseBatchConfig<Customer, Customer> {

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    @Bean(name = "customerReader")
    public ItemReader<Customer> createItemReader() {
        FlatFileItemReader<Customer> itemReader = new FlatFileItemReader<>();
        itemReader.setResource(new FileSystemResource("src/main/resources/data/customers.csv"));
        itemReader.setName("csvReader");
        itemReader.setLinesToSkip(1);
        itemReader.setLineMapper(linemapper());
       return itemReader;
    }

    private LineMapper<Customer> linemapper() {
        DefaultLineMapper<Customer> lineMapper =new DefaultLineMapper<>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("id", "firstName", "lastName", "email", "gender", "contactNo", "country", "dob");

        BeanWrapperFieldSetMapper<Customer> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Customer.class);

        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);

        return lineMapper;
    }

    @Override
    @Bean(name = "customerProcessor")
    public ItemProcessor<Customer, Customer> createItemProcessor() {
        return new CustomerProcessor();
    }

    @Override
    @Bean(name = "customerWriter")
    public ItemWriter<Customer> createItemWriter() {
        RepositoryItemWriter<Customer> writer= new RepositoryItemWriter<>();
        writer.setRepository(customerRepository);
        writer.setMethodName("save");
        return writer;
    }

    @Override
    @Bean(name = "customerStep")
    public Step step(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return  new StepBuilder("csv-step",jobRepository)
                .<Customer,Customer>chunk(10,transactionManager)
                .reader(createItemReader())
                .processor(createItemProcessor())
                .writer(createItemWriter())
                .build();
    }

    @Override
    @Bean(name = "customerRunJob")
    public Job runJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new JobBuilder("importCustomers",jobRepository)
                .flow(step(jobRepository,transactionManager))
                .end().build();
    }


}