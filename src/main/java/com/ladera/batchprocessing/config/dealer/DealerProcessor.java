package com.ladera.batchprocessing.config.dealer;

import com.ladera.batchprocessing.entity.Dealer;
import org.springframework.batch.item.ItemProcessor;

public class DealerProcessor implements ItemProcessor<Dealer, Dealer> {
    @Override
    public Dealer process(Dealer dealer) throws Exception {
        return dealer;
    }
}
