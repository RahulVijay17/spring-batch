package com.ladera.batchprocessing.config.customer;

import com.ladera.batchprocessing.entity.Customer;
import org.jetbrains.annotations.NotNull;
import org.springframework.batch.item.ItemProcessor;

public class CustomerProcessor implements ItemProcessor<Customer, Customer> {

    @Override
    public Customer process(@NotNull Customer customer) throws Exception {
        return customer;
    }
}