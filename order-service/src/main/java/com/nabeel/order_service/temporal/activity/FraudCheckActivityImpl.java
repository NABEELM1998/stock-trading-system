package com.nabeel.order_service.temporal.activity;

import io.temporal.activity.Activity;
import io.temporal.spring.boot.ActivityImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Component
@ActivityImpl(taskQueues = "order-processing")
public class FraudCheckActivityImpl implements FraudCheckActivity {
    private static final Logger logger = LoggerFactory.getLogger(FraudCheckActivityImpl.class);
    private static final Random random = new Random();

    @Override
    public FraudCheckResult performFraudCheck(FraudCheckRequest request) {
        logger.info("Performing fraud check for userId={}, symbol={}, quantity={}, amount={}", 
                request.getUserId(), request.getSymbol(), request.getQuantity(), request.getAmount());

        // Record heartbeat
        Activity.getExecutionContext().heartbeat("Performing fraud check");

        // Simulate random delay (500ms - 2s)
        try {
            long delay = ThreadLocalRandom.current().nextLong(500, 2000);
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new FraudCheckResult(false, "Fraud check interrupted");
        }

        // Simulate fraud check logic
        // In a real system, this would check against fraud detection systems
        boolean passed = true;
        String reason = "Fraud check passed";

        // Simulate some failure scenarios
        if (request.getAmount() != null && request.getAmount() > 1000000) {
            passed = false;
            reason = "Transaction amount exceeds fraud threshold";
        } else if (request.getUserId() != null && request.getUserId() % 100 == 0) {
            // Simulate 1% failure rate
            passed = false;
            reason = "Suspicious activity detected";
        }

        logger.info("Fraud check result: passed={}, reason={}", passed, reason);
        return new FraudCheckResult(passed, reason);
    }
}

