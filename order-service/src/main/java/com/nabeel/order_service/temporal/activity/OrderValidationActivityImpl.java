package com.nabeel.order_service.temporal.activity;

import com.nabeel.order_service.dto.CreateOrderRequest;
import com.nabeel.order_service.dto.ValidationResult;
import io.temporal.activity.Activity;
import io.temporal.spring.boot.ActivityImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
import com.nabeel.order_service.dto.MarketStatusResponse;


import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Set;

@Component
@ActivityImpl(taskQueues = "order-processing")
public class OrderValidationActivityImpl implements OrderValidationActivity {
    private static final Logger logger = LoggerFactory.getLogger(OrderValidationActivityImpl.class);
    
    private static final Set<String> VALID_SYMBOLS = Set.of("AAPL", "TSLA", "GOOGL", "MSFT", "AMZN", "META", "NVDA");
    private static final LocalTime MARKET_OPEN = LocalTime.of(9, 30);
    private static final LocalTime MARKET_CLOSE = LocalTime.of(16, 0);

    @Autowired
    private RestTemplate restTemplate;
    @Value("${market.service.base-url}")
    private String marketServiceBaseUrl;

    @Override
    public ValidationResult validateOrder(CreateOrderRequest request) {
        logger.info("Validating order: symbol={}, quantity={}, orderType={}, limitPrice={}", 
                request.getSymbol(), request.getQuantity(), request.getOrderType(), request.getLimitPrice());

        // Record heartbeat for long-running activities
        Activity.getExecutionContext().heartbeat("Validating order parameters");

        // Validate symbol
        if (request.getSymbol() == null || !VALID_SYMBOLS.contains(request.getSymbol().toUpperCase())) {
            return new ValidationResult(false, "Invalid symbol: " + request.getSymbol());
        }

        // Validate quantity
        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            return new ValidationResult(false, "Quantity must be positive");
        }

        // Validate order type
        if (request.getOrderType() == null || 
            (!"MARKET".equals(request.getOrderType()) && !"LIMIT".equals(request.getOrderType()))) {
            return new ValidationResult(false, "Invalid order type: " + request.getOrderType());
        }

        // Validate limit price for LIMIT orders
        if ("LIMIT".equals(request.getOrderType()) && 
            (request.getLimitPrice() == null || request.getLimitPrice().compareTo(BigDecimal.ZERO) <= 0)) {
            return new ValidationResult(false, "Limit price is required and must be positive for LIMIT orders");
        }

        // Check market hours
        LocalTime now = LocalTime.now();
        if (now.isBefore(MARKET_OPEN) || now.isAfter(MARKET_CLOSE)) {
            return new ValidationResult(false, 
                String.format("Market is closed. Trading hours: %s - %s", MARKET_OPEN, MARKET_CLOSE));
        }

  
        String marketStatusUrl = marketServiceBaseUrl + "/api/v1/market/status";
        boolean isMarketOpen;
        try {
            MarketStatusResponse response =
                    restTemplate.getForObject(marketStatusUrl, MarketStatusResponse.class);
            isMarketOpen = (response != null && Boolean.TRUE.equals(response.isOpen()));
        } catch (Exception e) {
            logger.error("Could not reach market status service: {}", e.getMessage());
            return new ValidationResult(false, "Could not verify market status");
        }
        if (!Boolean.TRUE.equals(isMarketOpen)) {
            return new ValidationResult(false, "Market is currently closed according to the service");
        }


        logger.info("Order validation successful");
        return new ValidationResult(true, "Order validation passed");
    }
}

