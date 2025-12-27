package com.nabeel.order_service.temporal.activity;

import io.temporal.activity.Activity;
import io.temporal.spring.boot.ActivityImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Component
@ActivityImpl(taskQueues = "order-processing")
public class OrderExecutionActivityImpl implements OrderExecutionActivity {
    private static final Logger logger = LoggerFactory.getLogger(OrderExecutionActivityImpl.class);
    private static final Random random = new Random();
    
    // Mock market prices
    private static final Map<String, Double> MARKET_PRICES = new HashMap<>();
    static {
        MARKET_PRICES.put("AAPL", 150.0);
        MARKET_PRICES.put("TSLA", 250.0);
        MARKET_PRICES.put("GOOGL", 140.0);
        MARKET_PRICES.put("MSFT", 380.0);
        MARKET_PRICES.put("AMZN", 130.0);
        MARKET_PRICES.put("META", 320.0);
        MARKET_PRICES.put("NVDA", 450.0);
    }

    @Override
    public ExecutionResult executeOrder(ExecutionRequest request) {
        logger.info("Executing order: orderId={}, symbol={}, side={}, quantity={}, orderType={}, limitPrice={}", 
                request.getOrderId(), request.getSymbol(), request.getSide(), 
                request.getQuantity(), request.getOrderType(), request.getLimitPrice());

        Activity.getExecutionContext().heartbeat("Executing order");

        try {
            // Get current market price
            Double marketPrice = MARKET_PRICES.getOrDefault(request.getSymbol().toUpperCase(), 100.0);
            
            // Add some randomness to simulate price movement
            double priceVariation = marketPrice * (0.99 + random.nextDouble() * 0.02); // ±1% variation
            Double executionPrice = priceVariation;

            // For LIMIT orders, check if limit price is acceptable
            if ("LIMIT".equals(request.getOrderType())) {
                if (request.getLimitPrice() == null) {
                    return new ExecutionResult(false, null, 0, 0.0, "Limit price is required for LIMIT orders");
                }
                
                if ("BUY".equals(request.getSide()) && executionPrice > request.getLimitPrice()) {
                    return new ExecutionResult(false, null, 0, 0.0, 
                        String.format("Market price %.2f exceeds limit price %.2f", executionPrice, request.getLimitPrice()));
                }
                
                if ("SELL".equals(request.getSide()) && executionPrice < request.getLimitPrice()) {
                    return new ExecutionResult(false, null, 0, 0.0, 
                        String.format("Market price %.2f below limit price %.2f", executionPrice, request.getLimitPrice()));
                }
            }

            // Calculate fees (0.1% of transaction value)
            Double totalValue = executionPrice * request.getQuantity();
            Double fees = totalValue * 0.001;

            // Simulate partial fills (90% chance of full fill, 10% chance of partial)
            Integer filledQuantity = request.getQuantity();
            if (random.nextDouble() < 0.1) {
                filledQuantity = (int) (request.getQuantity() * 0.8); // 80% fill
            }

            logger.info("Order executed: executionPrice={}, filledQuantity={}, fees={}", 
                    executionPrice, filledQuantity, fees);

            return new ExecutionResult(true, executionPrice, filledQuantity, fees, 
                String.format("Order executed at %.2f", executionPrice));
        } catch (Exception e) {
            logger.error("Error executing order", e);
            return new ExecutionResult(false, null, 0, 0.0, "Error executing order: " + e.getMessage());
        }
    }
}

