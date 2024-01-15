package com.ladera.batchprocessing.controller;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/batch")
public class CustomerBatchController {

    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    @Qualifier("customerRunJob")
    private Job customerJob;

    @PostMapping("/runCustomerJob")
    public String runBatchJob() {
        try {
            // Unique job parameter to ensure a new instance is created every time
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();

            jobLauncher.run(customerJob, jobParameters);

            return "Batch job has been triggered successfully.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error occurred while triggering the batch job.";
        }
    }
}

