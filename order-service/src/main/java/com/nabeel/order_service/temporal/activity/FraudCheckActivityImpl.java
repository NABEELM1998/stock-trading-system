package com.nabeel.order_service.temporal.activity;

import com.nabeel.order_service.dto.FraudCheckResult;
import com.nabeel.order_service.dto.WorkflowRequest;
import com.nabeel.order_service.exceptions.FraudCheckFailedException;
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
    public FraudCheckResult performFraudCheck(WorkflowRequest request) throws FraudCheckFailedException {
        logger.info("Performing fraud check for userId={}, symbol={}, quantity={}, amount={}",
                request.getUserId(), request.getSymbol(), request.getQuantity(), request.getLimitPrice());

        // Record heartbeat
        Activity.getExecutionContext().heartbeat("Performing fraud check");

        // Simulate random delay (500ms - 2s)
        try {
            long delay = ThreadLocalRandom.current().nextLong(500, 2000);
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("fraud check failed orderId-{}",request.getOrderId());
            throw new FraudCheckFailedException("fraud check failed");

        }

        logger.info("Fraud check result passes");
        return new FraudCheckResult(true, "success");
    }
}

