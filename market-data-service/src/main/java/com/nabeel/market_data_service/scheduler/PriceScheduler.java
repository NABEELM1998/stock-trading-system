package com.nabeel.market_data_service.scheduler;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class PriceScheduler {

    private final ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor();
    @Autowired
    private final MarketPriceUpdater priceUpdater;

    public PriceScheduler(MarketPriceUpdater priceUpdater) {
        this.priceUpdater = priceUpdater;
    }

    @PostConstruct
    public void start() {
        scheduler.scheduleAtFixedRate(
                priceUpdater,
                0,
                5,
                TimeUnit.SECONDS
        );
    }

    @PreDestroy
    public void shutdown() {
        scheduler.shutdown();
    }
}
